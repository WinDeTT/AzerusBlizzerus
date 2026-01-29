package org.windett.azerusBlizzerus.events.player;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerBaseConnectionEvents implements Listener {

    final Location worldFirstSpawnLocation = new Location(Bukkit.getWorld("world"), -881.932, 66.0, -1575.988, -89.8F, -0.5F);


    @EventHandler
    public void join(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
    }


    @EventHandler
    public void event(PlayerClientLoadedWorldEvent event) {}


    public void quit() {

    }

    public void asyncPreLogin() {

    }
}
