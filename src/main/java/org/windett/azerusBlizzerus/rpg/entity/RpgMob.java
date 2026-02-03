package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.content.ContentRpgSpawner;
import org.windett.azerusBlizzerus.events.custom.rpg.RpgEntityDeathEvent;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.UUID;

public class RpgMob implements RpgEntity, RpgDamageable{

    private final UUID uuid;
    private final ContentRpgEntity contentRpgEntity;
    private ContentRpgSpawner spawner;

    public RpgMob(UUID uuid, ContentRpgEntity contentRpgEntity, ContentRpgSpawner spawner) {
        this.uuid = uuid;
        this.contentRpgEntity = contentRpgEntity;
        this.spawner = spawner;
    }

    @Override
    public int getLevel() {
        return contentRpgEntity.getLevel();
    }

    @Override
    public double getHealth() {
        return asBukkitEntity().getHealth();
    }

    @Override
    public double getMaxHealth() {
        return contentRpgEntity.getMaxHealth();
    }

    @Override
    public void setHealth(int health) {
        asBukkitEntity().setHealth(Math.max(0.0, Math.min(health, getMaxHealth())));
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
    public void handleDamage() {

    }

    @Override
    public void handleDeath() {
        RpgDamageable rpgEntityLiving = this;
        Bukkit.getPluginManager().callEvent(new RpgEntityDeathEvent(this));
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

    @Override
    public LivingEntity asBukkitEntity() {
        return (LivingEntity) Bukkit.getEntity(this.uuid);
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
        return asBukkitEntity().isValid();
    }

    public ContentRpgSpawner getSpawner() {
        return spawner;
    }
}
