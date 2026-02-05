package org.windett.azerusBlizzerus.utils.cutscene.camera;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.windett.azerusBlizzerus.Main;

import java.util.List;

public class Cutscene_t1 {

    private static final int delay = 180;

    public static void startRecordedCameraFlying(final Player player, List<Location> points, int time, int interpol, LivingEntity target) {
        final Location playerLoc = player.getLocation();
        final Camera camera = new Camera(null, points.getFirst());
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(points.getFirst());

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            camera.setPosition(points.getFirst());
            camera.setWatcher(player);
            camera.moveByPoints(points, time, interpol);
            if (target == player) camera.setTarget(playerLoc);
            else camera.setTarget(target);

            new BukkitRunnable() {
                public void run() {
                    if (!camera.isMoving()) {
                        camera.remove();
                    }
                }
            }.runTaskTimer(Main.instance, delay, delay);

        }, 2L);
    }
}
