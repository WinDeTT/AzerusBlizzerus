package org.windett.azerusBlizzerus.rpg.entity;

import io.papermc.paper.entity.LookAnchor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.rpg.entity.spawner.ContentRpgSpawner;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.events.custom.rpg.RpgEntityDeathEvent;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.*;

public class RpgMob implements RpgEntity, RpgDamageable {

    private final UUID uuid;
    private final ContentRpgEntity contentRpgEntity;
    private ContentRpgSpawner spawner;
    private long lastAttackMillis;
    public DamagersTracker damagersTracker;
    private RpgPlayer target;
    private BukkitTask searchRunnable;
    private BukkitTask navigateRunnable;
    private BukkitTask attackRunnable;

    public RpgMob(UUID uuid, ContentRpgEntity contentRpgEntity, ContentRpgSpawner spawner) {
        this.uuid = uuid;
        this.contentRpgEntity = contentRpgEntity;
        this.spawner = spawner;
        this.lastAttackMillis = System.currentTimeMillis();
        if (contentRpgEntity.isAggressive()) {
            searchTarget();
        }
    }

    @Override
    public int getLevel() {
        return contentRpgEntity.getLevel();
    }

    @Override
    public double getHealth() {
        if (!isValid()) return 0.0;
        return asBukkitEntity().getHealth();
    }

    @Override
    public double getMaxHealth() {
        return contentRpgEntity.getMaxHealth();
    }

    @Override
    public void setHealth(double health) {
        if (!isValid()) return;
        asBukkitEntity().setHealth(Math.max(0.0, Math.min(health, getMaxHealth())));
    }

    @Override
    public void restoreHealth() {
        if (!isValid()) return;
        setHealth(getMaxHealth());

        List<RpgPlayer> nearbyPlayers = getNearbyPlayers(35);
        if (nearbyPlayers.isEmpty()) {
            return;
        }
        for (RpgPlayer rpgPlayer : nearbyPlayers) {
            Player player = (Player) rpgPlayer.asBukkitEntity();
            player.spawnParticle(Particle.HEART, asBukkitEntity().getEyeLocation().add(0, 1, 0), 1, 0, 0, 0);
        }
    }

    @Override
    public DamageStats getDamageStats() {
        return contentRpgEntity.getDamageStats();
    }

    @Override
    public DefenceStats getDefenceStats() {
        return contentRpgEntity.getDefenceStats();
    }

    @Override
    public long getAttackDelay() {
        return contentRpgEntity.getAttackDelay();
    }

    @Override
    public long getLastAttackMillis() {
        return lastAttackMillis;
    }

    @Override
    public void updateAttackCooldown() {
        this.lastAttackMillis = System.currentTimeMillis();
    }

    @Override
    public void handleDamage(double damage) {
        handleDamage(null, damage);
    }


    @Override
    public void handleDamage(RpgDamageable attacker, double damage) {
        if (!isValid()) return;

        if (attacker != null) {
            if (attacker instanceof RpgPlayer rpgPlayer) {
                if (damagersTracker == null) {
                    damagersTracker = new DamagersTracker(this, rpgPlayer, damage);
                    this.target = rpgPlayer;
                } else {
                    damagersTracker.merge(rpgPlayer, damage);
                    RpgPlayer oldTarget = this.target;
                    if (oldTarget == null || !damagersTracker.getDamageMap().containsKey(oldTarget)) {
                        this.target = rpgPlayer;
                    }
                }
                if (!contentRpgEntity.isAggressive()) {
                    if (navigateRunnable == null) {
                        navigateToTarget();
                        attackTarget();
                    }
                }
                attacker.updateAttackCooldown();
            }
            RpgEntity rpgEntitMob = this;
            RpgEntity rpgEntityAttacker = (RpgEntity) attacker;
            Bukkit.broadcastMessage("цель сущности " + rpgEntitMob.getName() + ":" + rpgEntityAttacker.getName());
        }

        List<RpgPlayer> nearestPlayers = getNearbyPlayers(35);
        if (!nearestPlayers.isEmpty()) {
            BlockData redstoneBlockData = Material.REDSTONE_BLOCK.createBlockData();
            for (RpgPlayer rpgPlayer : nearestPlayers) {
                Player player = (Player) rpgPlayer.asBukkitEntity();
                player.spawnParticle(Particle.BLOCK, asBukkitEntity().getEyeLocation().add(0, -0.5, 0), 50, 0.35, 0.4, 0.35, redstoneBlockData);
                player.playSound(asBukkitEntity().getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            }
        }

        if (damage >= getHealth()) {
            handleDeath();
            return;
        }
        asBukkitEntity().damage(damage);
    }

    @Override
    public void handleDeath() {
        if (!isValid()) return;
        Set<RpgPlayer> attackers = new HashSet<>();
        if (damagersTracker != null) {

            Map<RpgPlayer, Double> attackerMap = damagersTracker.getDamageMap();

            double mobXpReward = contentRpgEntity.getXpLoot();
            double totalDamage = attackerMap.values().stream().mapToDouble(Double::doubleValue).sum();

            if (totalDamage > 0) {
                for (RpgPlayer rpgPlayer : attackerMap.keySet()) {
                    double playerDamage = attackerMap.get(rpgPlayer);
                    float damagePercentage = (float) (playerDamage / totalDamage);
                    if (damagePercentage >= 0.05F) {
                        double playerXpReward = mobXpReward * damagePercentage;
                        rpgPlayer.getPlayerCharacter().addXp(playerXpReward);
                    }
                }
            }
            attackers = new HashSet<>(attackerMap.keySet());

            damagersTracker.stop();
            damagersTracker = null;
        }
        cleanup();
        asBukkitEntity().setHealth(0);
        Bukkit.getPluginManager().callEvent(new RpgEntityDeathEvent(this, attackers));
        if (spawner != null) {
            spawner.setKilled();
        }
    }

    public void searchTarget() {
        double maxDistance = contentRpgEntity.getAgroRange();

        searchRunnable = new BukkitRunnable() {
            List<RpgPlayer> sortedNearestPlayer;

            public void run() {
                if (!isValid()) {
                    cancel();
                    searchRunnable = null;
                    return;
                }
                Bukkit.broadcastMessage("Сущность ищет цель!");
                sortedNearestPlayer = getNearbyPlayers(maxDistance);
                if (sortedNearestPlayer != null) {
                    sortedNearestPlayer.sort(Comparator.comparingDouble(rp -> rp.getLocation().distanceSquared(getLocation())));

                    for (RpgPlayer rpgPlayer : sortedNearestPlayer) {
                        Player player = (Player) rpgPlayer.asBukkitEntity();
                        if (player.getGameMode().isInvulnerable()) continue;
                        LivingEntity mob = asBukkitEntity();
                        if (!mob.hasLineOfSight(player)) continue;
                        setTarget(rpgPlayer);
                        cancel();
                        searchRunnable = null;
                        CraftMob craftMob = (CraftMob) mob;
                        RpgEntityManager.clearPathfinders(asBukkitEntity());
                        craftMob.getHandle().goalSelector.addGoal(0, new FloatGoal(craftMob.getHandle()));
                        navigateToTarget();
                        attackTarget();
                        break;
                    }
                }
            }
        }.runTaskTimer(Main.instance, 30L, 30L);
    }

    public void navigateToTarget() {
        CraftMob rpgMob = (CraftMob) asBukkitEntity();
        navigateRunnable = new BukkitRunnable() {
            public void run() {
                if (getTarget() == null || !getTarget().isValid() || getTarget().getLocation().distanceSquared(getLocation()) >= contentRpgEntity.getAgroRange() * contentRpgEntity.getAgroRange() ||
                        (getTargetEntity() instanceof Player player && player.getGameMode().isInvulnerable())) {
                    cancel();
                    navigateRunnable = null;
                    if (attackRunnable != null) {
                        attackRunnable.cancel();
                        attackRunnable = null;
                    }
                    if (contentRpgEntity.isAggressive()) {
                        searchTarget();
                    }
                    rpgMob.getPathfinder().stopPathfinding();
                    RpgEntityManager.clearPathfinders(asBukkitEntity());
                    RpgEntityManager.initPathfinders(asBukkitEntity(), contentRpgEntity);
                    return;
                }
                if (getLocation().distanceSquared(getTarget().getLocation()) > (contentRpgEntity.getAttackRange() * contentRpgEntity.getAttackRange()) - 1.0) {
                    Bukkit.broadcastMessage("Ищет путь к цели!");
                    rpgMob.getHandle().getNavigation().moveTo(((CraftEntity) getTarget().asBukkitEntity()).getHandle(), 1.0);
                    asBukkitEntity().lookAt(((LivingEntity) getTarget().asBukkitEntity()).getEyeLocation(), LookAnchor.EYES);
                }
            }

        }.runTaskTimer(Main.instance, 0, 10L);
    }

    public void attackTarget() {
        attackRunnable = new BukkitRunnable() {

            public void run() {
                if (getTarget() == null || !getTarget().isValid()) return;
                if (getLocation().distanceSquared(getTarget().getLocation()) > contentRpgEntity.getAttackRange() * contentRpgEntity.getAttackRange())
                    return;
                Bukkit.broadcastMessage("Пытается атаковать!");
                asBukkitEntity().swingMainHand();
                asBukkitEntity().lookAt(((LivingEntity) getTarget().asBukkitEntity()).getEyeLocation(), LookAnchor.EYES);
                attack(getTarget(), contentRpgEntity.getDamageStats().getPhysicalDamage());
            }

        }.runTaskTimer(Main.instance, 0, getAttackDelay());
    }

    public void attack(RpgPlayer rpgPlayer, double damage) {
        if (rpgPlayer == null) return;
        rpgPlayer.handleDamage(this, damage);
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return contentRpgEntity.getName();
    }

    @Override
    public String getContext() {
        return Main.tweakManager.getContextManager().getEntityContext(asBukkitEntity()).getContextName();
    }

    @Override
    public LivingEntity asBukkitEntity() {
        return (LivingEntity) Bukkit.getEntity(this.uuid);
    }

    @Override
    public World getWorld() {
        if (!isValid()) return null;
        return asBukkitEntity().getWorld();
    }

    @Override
    public Location getLocation() {
        if (!isValid()) return null;
        return asBukkitEntity().getLocation();
    }

    @Override
    public boolean isValid() {
        return asBukkitEntity() != null && asBukkitEntity().isValid();
    }

    @Override
    public List<RpgPlayer> getNearbyPlayers(double distance) {
        if (!isValid()) return null;
        final WorldContext context = Main.tweakManager.getContextManager().getEntityContext(asBukkitEntity());
        if (context == null) return List.of();
        List<RpgPlayer> nearbyPlayers = new ArrayList<>();
        for (Player player : asBukkitEntity().getLocation().getNearbyPlayers(distance)) {
            RpgPlayer rpgPlayer = (RpgPlayer) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(player);
            if (rpgPlayer == null) continue;
            if (!rpgPlayer.getContext().equals(context.getContextName())) continue;
            nearbyPlayers.add(rpgPlayer);
        }
        return nearbyPlayers;
    }

    @Override
    public void handleUnregister() {
        Main.rpgSystemManager.getRpgEntityManager().unregisterRpgEntity(this);
    }

    @Override
    public void cleanup() {
        if (searchRunnable != null) {
            searchRunnable.cancel();
            searchRunnable = null;
        }
        if (navigateRunnable != null) {
            navigateRunnable.cancel();
            navigateRunnable = null;
        }
        if (attackRunnable != null) {
            attackRunnable.cancel();
            attackRunnable = null;
        }
        handleUnregister();
    }

    public ContentRpgSpawner getSpawner() {
        return spawner;
    }

    public RpgPlayer getTarget() {
        return target;
    }

    public void setTarget(RpgPlayer target) {
        this.target = target;
    }

    public Player getTargetEntity() {
        if (this.target == null) return null;
        return (Player) target.asBukkitEntity();
    }

    public DamagersTracker getDamagersTracker() {
        return damagersTracker;
    }

    public void destroy() {
        if (isValid()) return;
        handleDeath();
    }
}
