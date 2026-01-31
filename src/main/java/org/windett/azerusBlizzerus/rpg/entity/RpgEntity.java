package org.windett.azerusBlizzerus.rpg.entity;

import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface RpgEntity {
    UUID getUuid();
    String getName();
    Location getLocation();
    World getWorld();

    // RPG-характеристики
    int getLevel();
    double getHealth();
    double getMaxHealth();
    double getMana();
    double getMaxMana();
    
    boolean isAlive();

    @Nullable
    default Entity asBukkitEntity() {
        return Bukkit.getEntity(getUuid());
    }
}
