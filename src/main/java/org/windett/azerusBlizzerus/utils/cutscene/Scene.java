package org.windett.azerusBlizzerus.utils.cutscene;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.utils.cutscene.camera.Camera;
import org.windett.azerusBlizzerus.utils.cutscene.camera.CameraManager;

import java.io.FileNotFoundException;
import java.util.*;

public class Scene {

    private final UUID sceneId;
    private int tick = 0;
    private final long duration;
    private BukkitTask sceneRunnable;
    private Runnable onStart;
    private Runnable onEnd;
    public final Map<Integer, Camera> cameraList = new HashMap<>();
    private final Map<Integer, List<Location>> cameraPath = new HashMap<>();
    private final Map<Integer, Runnable> tickTriggers = new HashMap<>();
    private final Map<Integer, LivingEntity> actors = new HashMap<>();
    public SceneWatcher sceneWatcher;


    public Scene(int duration) {
        this.sceneId = UUID.randomUUID();
        this.duration = duration;
    }

    public void setSceneWatcher(Player player, Boolean createFakeModel) {
        sceneWatcher = new SceneWatcher(player, createFakeModel);
    }
    public void removeSceneWatcher() {
        sceneWatcher.getWatcher().setSpectatorTarget(null);
        sceneWatcher.getWatcher().setGameMode(GameMode.SURVIVAL);
        sceneWatcher.getWatcher().teleport(sceneWatcher.getWatcherLastLocation());
        if (sceneWatcher.fakePlayerExists()) sceneWatcher.removeFakePlayer();
        this.sceneWatcher = null;
    }

    public SceneWatcher getSceneWatcher() {
        return this.sceneWatcher;
    }

    public void createCamera(int id, Location location) {
        if (id <= 0) return;
        if (id > 100) return;
        Camera camera = new Camera(this, location);
        camera.setUpInScene(this);
        cameraList.put(id, camera);
        cameraPath.put(id, null);
    }

    public void setCameraPath(int cameraId, String fileName) throws FileNotFoundException {
        if (!cameraList.containsKey(cameraId)) {
            return;
        }
        final Location cameraLocation = cameraList.get(cameraId).getCameraEntity().getLocation();
        List<Location> points = Main.tweakManager.getCameraManager().loadCameraPoints(cameraLocation.getWorld(), fileName);
        if (points == null) cameraPath.put(cameraId, List.of(cameraLocation, cameraLocation));
        else cameraPath.put(cameraId, points);
    }

    public void createTickTrigger(int tick, Runnable task) {
        if (tick < 0 || tick > duration) return;
        tickTriggers.put(tick, task);
    }

    private void executeTickTrigger(int tick) {
        Runnable task = tickTriggers.get(tick);
        if (task == null) return;
        task.run();
    }

    public LivingEntity createActor(int id, EntityType type, Location location) {
        if (actors.containsKey(id)) return null;
        LivingEntity actor = (LivingEntity) location.getWorld().spawnEntity(location, type);
        actors.put(id, actor);
        return actor;
    }

    public void clearActors() {
        if (actors.isEmpty()) return;
        for (int id : actors.keySet()) {
            LivingEntity actor = actors.get(id);
            if (actor.isValid() && actor.isTicking()) {
                actor.remove();
            }
        }
    }

    public void runScene() {
        if (this.onStart != null) onStart.run();
        sceneRunnable = new BukkitRunnable() {
            public void run() {
                if (tickTriggers.containsKey(tick)) {
                    executeTickTrigger(tick);
                    /*/
                    Runnable task = tickTriggers.get(tick);
                    if (task != null) task.run();
                     */
                }
                tick++;
                if (isSceneEnded()) {
                    cancel();
                    if (onEnd != null) onEnd.run();
                }
            }
        }.runTaskTimer(Main.instance, 1L, 1L);
    }

    public boolean isSceneEnded() {
        return tick >= duration;
    }
    public @NotNull Camera getCamera(int id) {
        return cameraList.get(id);
    }
    public List<Location> getCameraPoints(int cameraId) {
        return new ArrayList<>(cameraPath.get(cameraId));
    }

    public void removeCamera(int id) {
        if (cameraList.containsKey(id)) {
            cameraList.get(id).remove();
        }
        cameraList.remove(id);
        cameraPath.remove(id);
    }

    public void launchCamera(int cameraId, int duration, int smooth) {
        List<Location> points = getCameraPoints(cameraId);
        if (points == null) return;
        Camera camera = getCamera(cameraId);
        camera.moveByPoints(points, duration, smooth);
    }

    public void clearAllCameras() {
        for (Camera camera : cameraList.values()) {
            camera.remove();
            if (sceneWatcher != null) {
                removeSceneWatcher();
            }
        }
    }

    public void executeAtStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public void executeAtEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    public UUID getSceneId() {
        return this.sceneId;
    }
    public BukkitTask getSceneRunnable() {
        return this.sceneRunnable;
    }
}
