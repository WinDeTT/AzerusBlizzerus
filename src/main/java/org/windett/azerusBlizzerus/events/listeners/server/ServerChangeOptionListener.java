package org.windett.azerusBlizzerus.events.listeners.server;

import io.papermc.paper.event.world.WorldDifficultyChangeEvent;
import org.bukkit.Difficulty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerChangeOptionListener implements Listener {

    @EventHandler
    public void diff(WorldDifficultyChangeEvent event) {
        event.getWorld().setDifficulty(Difficulty.NORMAL);
    }
}
