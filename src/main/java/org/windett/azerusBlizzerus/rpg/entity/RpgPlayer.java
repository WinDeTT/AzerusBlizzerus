package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.UUID;

public class RpgPlayer implements RpgEntity, RpgDamageable {

    private final Player bukkitPlayer;
    private final String name;

    public RpgPlayer(Player player) {
        this.bukkitPlayer = player;
        this.name = player.getName();
    }


    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public double getHealth() {
        return this.bukkitPlayer.getHealth();
    }

    @Override
    public double getMaxHealth() {
        return this.bukkitPlayer.getMaxHealth();
    }

    @Override
    public void setHealth(int health) {
        this.bukkitPlayer.setHealth(health);
    }

    @Override
    public DamageStats getDamageStats() {
        return null;
    }

    @Override
    public DefenceStats getDefenceStats() {
        return null;
    }

    @Override
    public void onDamage() {

    }

    @Override
    public void onDeath() {

    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public Entity asBukkitEntity() {
        return this.bukkitPlayer;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
