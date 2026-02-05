package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.windett.azerusBlizzerus.context.WorldContext;

import java.util.UUID;

public interface RpgEntity {

    UUID getUniqueId();
    Entity asBukkitEntity();
    String getContext();
    String getName();
    World getWorld();
    Location getLocation();
    boolean isValid();
    void handleUnregister();
}
