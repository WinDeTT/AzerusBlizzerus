package org.windett.azerusBlizzerus.utils.cutscene.camera;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.windett.azerusBlizzerus.Main;

import java.util.List;

public class Cutscene_t1 {


    public static void startRecordedCameraFlying(final Player player, List<Location> points, int time, int interpol, LivingEntity target, Camera.ShakeIntensive intensive) {
        final Location playerLoc = player.getLocation();
        final Camera camera = new Camera(null, points.getFirst());

        camera.setOnMoveEnding(() -> {
            if (player.isOnline()) {
                player.setSpectatorTarget(null);
                player.teleport(playerLoc);
            }
            camera.remove();
        });

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(points.getFirst());

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            camera.setPosition(points.getFirst());
            if (intensive != null) {
                switch (intensive) {
                    case LOW -> {
                        camera.setShaking(10, Camera.ShakeIntensive.LOW);
                    }
                    case MEDIUM -> {
                        camera.setShaking(7, Camera.ShakeIntensive.MEDIUM);
                    }
                    case HIGH -> {
                        camera.setShaking(7, Camera.ShakeIntensive.HIGH);
                    }
                    case HYPER -> {
                        camera.setShaking(6, Camera.ShakeIntensive.HYPER);
                    }
                    case HYPER_TURBO -> {
                        camera.setShaking(5, Camera.ShakeIntensive.HYPER_TURBO);
                    }
                    case CRAZY_KID -> {
                        camera.setShaking(5, Camera.ShakeIntensive.CRAZY_KID);
                    }
                    default -> {
                        camera.setShaking(0, null);
                    }
                }
            }
            camera.setWatcher(player);
            camera.moveByPoints(points, time, interpol);
            if (target == player) camera.setTarget(playerLoc);
            else camera.setTarget(target);
        }, 1L);
    }
}
