package org.windett.azerusBlizzerus.events.custom.context;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.context.WorldContext;

public class ContextUnregisterEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    public WorldContext getContext() {
        return context;
    }

    private final WorldContext context;

    public ContextUnregisterEvent(WorldContext context) {
        this.context = context;
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
