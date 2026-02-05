package org.windett.azerusBlizzerus.events.custom.rpg;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.rpg.entity.RpgDamageable;
import org.windett.azerusBlizzerus.rpg.entity.RpgPlayer;

import java.util.List;
import java.util.Set;

public class RpgEntityDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public RpgDamageable getRpgDamageable() {
        return rpgDamageable;
    }

    private final RpgDamageable rpgDamageable;

    public RpgEntityDeathEvent(RpgDamageable rpgDamageable, Set<RpgPlayer> attackers) {
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
