package org.windett.azerusBlizzerus.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public interface RpgEntity {
    UUID getUuid();
    String getName();
    EntityType getType();

    // Позиция
    Location getLocation();
    World getWorld();

    // RPG характеристики
    /*
    Сюда нужно вставить
     */

    // Состояние
    boolean isAlive();
    double getHealth();
    double getMaxHealth();
    void setHealth(double health);

    // Боевая система
    void damage(double amount);
    void heal(double amount);

    // События
    void onTick();
    void onDeath();

}
