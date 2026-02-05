package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.rpg.player.data.PlayerCharacter;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RpgPlayer implements RpgEntity, RpgDamageable {

    private final Player bukkitPlayer;
    private final String name;
    private final PlayerCharacter playerCharacter;
    private long attackCooldown;
    private long lastAttackMillis;

    public RpgPlayer(Player player) {
        this.bukkitPlayer = player;
        this.name = player.getName();
        this.playerCharacter = new PlayerCharacter(this);
        this.attackCooldown = 350L;
        this.lastAttackMillis = System.currentTimeMillis();
    }


    @Override
    public int getLevel() {
        return getPlayerCharacter().getLevel();
    }

    @Override
    public double getHealth() {
        return this.bukkitPlayer.getHealth();
    }

    @Override
    public double getMaxHealth() {
        return Objects.requireNonNull(this.bukkitPlayer.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
    }

    @Override
    public void setHealth(double health) {
        this.bukkitPlayer.setHealth(health);
    }
    @Override
    public void restoreHealth() {
        setHealth(getMaxHealth());
    }

    @Override
    public DamageStats getDamageStats() {
        return getPlayerCharacter().getDamageStats();
    }

    @Override
    public DefenceStats getDefenceStats() {
        return getPlayerCharacter().getDefenceStats();
    }

    @Override
    public long getAttackCooldown() {
        return attackCooldown;
    }

    @Override
    public long getLastAttackMillis() {
        return lastAttackMillis;
    }

    @Override
    public void updateAttackCooldown() {
        lastAttackMillis = System.currentTimeMillis();
    }

    @Override
    public void handleDamage(double damage) {
        handleDamage(null, damage);
    }

    @Override
    public void handleDamage(RpgDamageable attacker, double damage) {
        bukkitPlayer.damage(damage);
        List<RpgPlayer> nearestPlayers = getNearbyPlayers();
        if (nearestPlayers.isEmpty()) return;
        BlockData redstoneBlockData = Material.REDSTONE_BLOCK.createBlockData();
        for (RpgPlayer rpgPlayer : nearestPlayers) {
            Player player = (Player) rpgPlayer.asBukkitEntity();
            player.spawnParticle(Particle.BLOCK, asBukkitEntity().getLocation(), 50, 0.15,0.3,0.15, redstoneBlockData);
            player.playSound(asBukkitEntity().getLocation(), Sound.ENTITY_PLAYER_HURT, 1,1);
        }
    }

    @Override
    public void handleDeath() {
        playerCharacter.takeXp(playerCharacter.getXp() / 100 * 1);
        updateLevelScale();
    }

    @Override
    public UUID getUniqueId() {
        return asBukkitEntity().getUniqueId();
    }

    @Override
    public Entity asBukkitEntity() {
        return this.bukkitPlayer;
    }

    @Override public String getContext() {
        return Main.tweakManager.getContextManager().getEntityContext(asBukkitEntity()).getContextName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public World getWorld() {
        return asBukkitEntity().getWorld();
    }

    @Override
    public Location getLocation() {
        return asBukkitEntity().getLocation();
    }

    @Override
    public boolean isValid() {
        return Main.rpgSystemManager.getRpgEntityManager().getRpgEntityContainerMap().containsKey(getUniqueId());
    }

    @Override
    public List<RpgPlayer> getNearbyPlayers() {
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

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
    public void updateLevelScale() {
        bukkitPlayer.setLevel(playerCharacter.getLevel());
        float progress = (float) (playerCharacter.getXp() / playerCharacter.getRequirementXp(getLevel()));
        bukkitPlayer.setExp(progress);
    }
}
