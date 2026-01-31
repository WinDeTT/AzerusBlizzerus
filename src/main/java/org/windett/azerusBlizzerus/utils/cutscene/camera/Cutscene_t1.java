package org.windett.azerusBlizzerus.utils.cutscene.camera;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.events.player.PlayerJoinQuitListener;
import org.windett.azerusBlizzerus.rpg.player.data.PlayerData;

import java.util.List;

public class Cutscene_t1 {

    private static final int delay = 180;

    public static void startRecordedCameraFlying(final Player player, List<Location> points, int time, int interpol, LivingEntity target) {
        final PlayerData playerData = PlayerJoinQuitListener.PLAYERS.get(player.getUniqueId());
        if (playerData == null) return;
        final Location playerLoc = player.getLocation();
        final Camera camera = new Camera(null, points.getFirst());
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(points.getFirst());

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            playerData.setInCutscene(true);
            camera.setPosition(points.getFirst());
            camera.setWatcher(player);
            camera.moveByPoints(points, time, interpol);
            if (target == player) camera.setTarget(playerLoc);
            else camera.setTarget(target);

            new BukkitRunnable() {
                public void run() {
                    if (!camera.isMoving()) {
                        playerData.setInCutscene(false);
                        camera.remove();
                    }
                }
            }.runTaskTimer(Main.instance, delay, delay);

        }, 20L);
    }
}
