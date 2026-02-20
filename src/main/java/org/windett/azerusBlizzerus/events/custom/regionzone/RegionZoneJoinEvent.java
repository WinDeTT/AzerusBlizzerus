package org.windett.azerusBlizzerus.events.custom.regionzone;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.utils.regionzone.RegionZone;

public class RegionZoneJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public Entity getEntity() {
        return entity;
    }

    public RegionZone getRegionZone() {
        return regionZone;
    }

    private final Entity entity;
    private final RegionZone regionZone;

    public RegionZoneJoinEvent(Entity entity, RegionZone regionZone) {
        this.entity = entity;
        this.regionZone = regionZone;
    }


    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
