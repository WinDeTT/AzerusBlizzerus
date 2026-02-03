package org.windett.azerusBlizzerus.events.custom.rpg.entity.mob;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.rpg.entity.RpgDamageable;

public class RpgMobSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public RpgDamageable getRpgDamageable() {
        return rpgDamageable;
    }

    private final RpgDamageable rpgDamageable;

    public RpgMobSpawnEvent(RpgDamageable rpgDamageable) {
        this.rpgDamageable = rpgDamageable;
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
