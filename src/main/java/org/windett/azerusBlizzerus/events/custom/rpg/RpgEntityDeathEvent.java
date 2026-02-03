package org.windett.azerusBlizzerus.events.custom.rpg;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.rpg.entity.RpgDamageable;

public class RpgEntityDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public RpgDamageable getRpgDamageable() {
        return rpgDamageable;
    }

    private final RpgDamageable rpgDamageable;

    public RpgEntityDeathEvent(RpgDamageable rpgDamageable) {
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
