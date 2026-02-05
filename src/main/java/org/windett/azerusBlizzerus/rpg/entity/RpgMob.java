package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.content.ContentRpgSpawner;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.events.custom.rpg.RpgEntityDeathEvent;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.*;

public class RpgMob implements RpgEntity, RpgDamageable{

    private final UUID uuid;
    private final ContentRpgEntity contentRpgEntity;
    private ContentRpgSpawner spawner;
    private long lastAttackMillis;
    public DamagersTracker damagersTracker;

    public RpgMob(UUID uuid, ContentRpgEntity contentRpgEntity, ContentRpgSpawner spawner) {
        this.uuid = uuid;
        this.contentRpgEntity = contentRpgEntity;
        this.spawner = spawner;
        this.lastAttackMillis = System.currentTimeMillis();
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
        setHealth(getMaxHealth());

        List<RpgPlayer> nearbyPlayers = getNearbyPlayers();
        if (nearbyPlayers.isEmpty()) {
            return;
        }
        for (RpgPlayer rpgPlayer : nearbyPlayers) {
            Player player = (Player) rpgPlayer.asBukkitEntity();
            player.spawnParticle(Particle.HEART, asBukkitEntity().getEyeLocation().add(0,1,0), 1, 0,0,0);
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
    public long getAttackCooldown() {
        return contentRpgEntity.getAttackCooldown();
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
                } else damagersTracker.merge(rpgPlayer, damage);
            }
            attacker.updateAttackCooldown();
        }

        List<RpgPlayer> nearestPlayers = getNearbyPlayers();
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
                        rpgPlayer.asBukkitEntity().sendMessage("Вы нанесли " + String.format("%.3f", playerDamage) + " урона!");
                    }
                }
            }
            attackers = attackerMap.keySet();

            damagersTracker.stop();
            damagersTracker = null;
        }
        asBukkitEntity().setHealth(0);
        Bukkit.getPluginManager().callEvent(new RpgEntityDeathEvent(this,  attackers));
        if (spawner != null) {
            spawner.setKilled();
        }
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return contentRpgEntity.getName();
    }

    @Override public String getContext() {
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
        return asBukkitEntity().isValid();
    }

    @Override
    public List<RpgPlayer> getNearbyPlayers() {
        if (!isValid()) return null;
        final WorldContext context = Main.tweakManager.getContextManager().getEntityContext(asBukkitEntity());
        if (context == null) return List.of();
        List<RpgPlayer> nearbyPlayers = new ArrayList<>();
        for (Player player : asBukkitEntity().getLocation().getNearbyPlayers(35,35,35)) {
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

    public ContentRpgSpawner getSpawner() {
        return spawner;
    }
    public DamagersTracker getDamagersTracker() {
        return damagersTracker;
    }
    public void destroy() {
        if (isValid()) return;
        handleDeath();
    }
}
