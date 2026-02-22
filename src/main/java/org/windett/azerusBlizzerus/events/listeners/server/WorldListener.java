package org.windett.azerusBlizzerus.events.listeners.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.spawner.ContentRpgSpawner;

public class WorldListener implements Listener {

    @EventHandler
    public void loadWorld(WorldLoadEvent event) {


        for (ContentRpgSpawner spawner : Main.rpgSystemManager.getRpgEntityManager().getRpgMobSpawnerMap().values()) {
            if (!spawner.getWorldName().equals(event.getWorld().getName())) continue;
            spawner.startSpawnPointWork();
        }
    }

    @EventHandler
    public void unloadWorld(WorldUnloadEvent event) {


        for (ContentRpgSpawner spawner : Main.rpgSystemManager.getRpgEntityManager().getRpgMobSpawnerMap().values()) {
            if (!spawner.getWorldName().equals(event.getWorld().getName())) continue;
            spawner.stopSpawnPointWork();
        }
    }
}
