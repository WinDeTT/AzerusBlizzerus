package org.windett.azerusBlizzerus.events.listeners.rpg.entity.mob;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.windett.azerusBlizzerus.events.custom.rpg.entity.mob.RpgMobSpawnEvent;
import org.windett.azerusBlizzerus.rpg.entity.RpgMob;

public class RpgMobListener implements Listener {

    @EventHandler
    public void onSpawn(RpgMobSpawnEvent event) {
        RpgMob rpgMob = (RpgMob) event.getRpgDamageable();
        Bukkit.broadcastMessage("РПГ-моб появился: " + rpgMob.getName() + " (" + rpgMob.getLocation() + ")");
    }
}
