package org.windett.azerusBlizzerus.events.listeners.entity.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.rpg.entity.RpgPlayer;

public class PlayerListenerJoinQuit implements Listener {


    private static final ContextManager contextManager = Main.tweakManager.getContextManager();
    final Location worldFirstSpawnLocation = new Location(Bukkit.getWorld("world"), -881.932, 66.0, -1575.988, -89.8F, -0.5F);

    @EventHandler(priority = EventPriority.MONITOR)
    public void asyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!Main.instance.isServerIsReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Please, wait few seconds");
            return;
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        contextManager.setUpEntityContext(player, "global");
        event.joinMessage(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(worldFirstSpawnLocation);
        RpgPlayer rpgPlayer = new RpgPlayer(player);
        Main.rpgSystemManager.getRpgEntityManager().registerRpgEntity(player.getUniqueId(), rpgPlayer);
        rpgPlayer.updateLevelScale();
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
    }
}
