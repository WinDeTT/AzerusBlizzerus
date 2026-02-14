package org.windett.azerusBlizzerus.utils.cutscene.camera;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.persistenceData.NamespacedHelper;
import org.windett.azerusBlizzerus.utils.cutscene.Scene;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Camera {

    private final ItemDisplay cameraEntity;
    final ItemStack displayStack = new ItemStack(Material.AIR);
    private Scene inScene;
    private static Random rnd = new Random();


    private Player cameraWatcher;
    private Object target = null;
    private int shaking = 0;

    public enum ShakeIntensive {
        LOW(1),
        MEDIUM(3),
        HIGH(6),
        HYPER(10),
        HYPER_TURBO(15);

        public int getIntensiveAmount() {
            return intensiveAmount;
        }

        private final int intensiveAmount;

        ShakeIntensive(int intensiveAmount) {
            this.intensiveAmount = intensiveAmount;
        }
    }

    private ShakeIntensive shakeIntensive = ShakeIntensive.LOW;

    public BukkitTask getMoveRunnable() {
        return moveRunnable;
    }

    public BukkitTask getPointMoveRunnable() {
        return pointMoveRunnable;
    }

    private BukkitTask moveRunnable = null;
    private BukkitTask pointMoveRunnable = null;
    private Runnable onMoveEnding = null;


    public Camera(Scene scene, Location location) {
        this.inScene = scene;
        this.cameraEntity = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        this.cameraEntity.getPersistentDataContainer().set(NamespacedHelper.isCameraKey, PersistentDataType.BOOLEAN, true);
        this.cameraEntity.setItemStack(displayStack);
        Main.tweakManager.getCameraManager().getWorkingCameras().add(this);
    }

    public void moveTo(Location point, long time, int interpolation) { // thanks DeepSeek
        if (moveRunnable != null && !moveRunnable.isCancelled()) {
            moveRunnable.cancel();
        }
        cameraEntity.setTeleportDuration(interpolation);
        Location currentLoc = cameraEntity.getLocation();
        Location step = point.subtract(currentLoc).multiply(1F / time);

        float startYaw = currentLoc.getYaw();
        float startPitch = currentLoc.getPitch();
        float targetYaw = point.getYaw();
        float targetPitch = point.getPitch();

        float yawStep = getShortestAngleStep(startYaw, targetYaw, time);
        float pitchStep = (targetPitch - startPitch) / time;

        moveRunnable = new BukkitRunnable() {
            int endTimer = 0;
            float shakePhase = 0;

            // НАСТРОЙКИ ЦИКЛИЧЕСКОГО ДРОЖАНИЯ
            float shakeSpeed = 0.3f; // Скорость волны (рад/тик)
            float waveRatio = 1.3f;  // Соотношение yaw/pitch волн

            @Override
            public void run() {
                checkCameraAliveStatus();
                currentLoc.add(step);

                // БАЗОВЫЕ углы (без дрожания)
                float baseYaw = startYaw + (yawStep * endTimer);
                float basePitch = startPitch + (pitchStep * endTimer);

                if (target == null) {
                    currentLoc.setRotation(baseYaw, basePitch);
                } else {
                    Vector direction = null;
                    if (target instanceof LivingEntity) {
                        direction = ((LivingEntity) target).getEyeLocation()
                                .clone().subtract(currentLoc).toVector().normalize();
                    } else if (target instanceof Location) {
                        direction = ((Location) target)
                                .clone().subtract(currentLoc).toVector().normalize();
                    }
                    if (direction != null) {
                        currentLoc.setDirection(direction);
                        baseYaw = currentLoc.getYaw();
                        basePitch = currentLoc.getPitch();
                    }
                }

                // ПРИМЕНЯЕМ ДРОЖАНИЕ (если включено)
                if (shaking > 0 && shakeIntensive != null) {
                    // 1. ЦИКЛИЧЕСКОЕ дрожание (синусоидальные волны)
                    shakePhase += shakeSpeed;
                    if (shakePhase > Math.PI * 2) shakePhase -= (Math.PI * 2);

                    // Вычисляем силу дрожания: shaking * интенсивность из enum
                    float totalShakePower = shaking * (shakeIntensive.getIntensiveAmount() / 10.0f);

                    // Волнообразное дрожание
                    float yawWave = (float) Math.sin(shakePhase) * totalShakePower * 2;
                    float pitchWave = (float) Math.cos(shakePhase * waveRatio) * totalShakePower;

                    // Случайная составляющая (зависит от интенсивности)
                    rnd = ThreadLocalRandom.current();
                    float randomFactor = shakeIntensive.getIntensiveAmount() / 20.0f; // 0.05 для LOW, 0.5 для HYPER
                    float yawRandom = (rnd.nextFloat() * 2 - 1) * totalShakePower * randomFactor;
                    float pitchRandom = (rnd.nextFloat() * 2 - 1) * totalShakePower * randomFactor * 0.7f;

                    // Суммируем все компоненты
                    float finalYaw = baseYaw + yawWave + yawRandom;
                    float finalPitch = basePitch + pitchWave + pitchRandom;

                    currentLoc.setRotation(
                            normalizeYaw(finalYaw),
                            clampPitch(finalPitch)
                    );

                    // 2. ДОПОЛНИТЕЛЬНО: Для HYPER - иногда резкие толчки
                    if (shakeIntensive == ShakeIntensive.HYPER && rnd.nextFloat() < 0.1f) {
                        // Резкий толчок (10% шанс каждый тик)
                        float jerkYaw = (rnd.nextFloat() * 2 - 1) * shaking * 0.5f;
                        float jerkPitch = (rnd.nextFloat() * 2 - 1) * shaking * 0.3f;
                        currentLoc.setRotation(
                                normalizeYaw(currentLoc.getYaw() + jerkYaw),
                                clampPitch(currentLoc.getPitch() + jerkPitch)
                        );
                    }

                } else {
                    // Без дрожания - просто базовые углы
                    currentLoc.setRotation(baseYaw, basePitch);
                }

                cameraEntity.teleport(currentLoc);
                endTimer++;

                if (endTimer >= time) {
                    cancel();
                    moveRunnable = null;
                }
            }

            private float clampPitch(float pitch) {
                return Math.max(-90, Math.min(90, pitch));
            }

        }.runTaskTimer(Main.instance, 0L, 1L);
    }

    // Вспомогательный метод (добавь в класс)
    private float normalizeYaw(float yaw) {
        yaw %= 360;
        if (yaw < -180) yaw += 360;
        else if (yaw > 180) yaw -= 360;
        return yaw;
    }

    public void moveByPoints(List<Location> points, long time, int interpolation) {
        if (pointMoveRunnable != null && !pointMoveRunnable.isCancelled()) {
            pointMoveRunnable.cancel();
        }
        int locationsCount = points.size();
        if (locationsCount < 1) return;
        moveTo(points.get(1), time / locationsCount, interpolation);
        pointMoveRunnable = new BukkitRunnable() {
            int current = 1;
            int step = 0;

            @Override
            public void run() {
                checkCameraAliveStatus();
                step++;
                if (step >= time / locationsCount) {
                    current++;
                    if (current >= locationsCount) {
                        cancel();
                        pointMoveRunnable = null;
                        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                            if (onMoveEnding != null) {
                                onMoveEnding.run();
                            }
                        },interpolation);
                    } else {
                        if (current <= locationsCount - 1) {
                            moveTo(points.get(current), time / locationsCount, interpolation);
                            step = 0;
                        }
                    }
                }
            }
        }.runTaskTimer(Main.instance, 0L, 1L);
    }

    public void setPosition(Location location) {
        if (isMoving()) return;
        this.cameraEntity.setTeleportDuration(0);
        this.cameraEntity.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public void setTarget(LivingEntity entity) {
        this.target = (LivingEntity) entity;
    }

    public void setTarget(Location location) {
        this.target = (Location) location;
    }

    public void removeTarget() {
        if (this.target == null) return;
        this.target = null;
    }

    public void setShaking(int shaking, ShakeIntensive shakeIntensive) {
        if (shaking < 0) shaking = 0;
        if (shaking > 40) shaking = 40;
        this.shaking = shaking;
        this.shakeIntensive = shakeIntensive;
    }

    public void setWatcher(Player player) {
        cameraWatcher = player;
        if (cameraWatcher == null) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(cameraEntity);
    }

    public void setOnMoveEnding(Runnable task) {
        this.onMoveEnding = task;
    }

    public void remove() {
        stopCamera();
        cameraEntity.remove();
        Main.tweakManager.getCameraManager().getWorkingCameras().remove(this);
    }

    public boolean isCameraAlive() {
        return cameraEntity.isValid() || !cameraEntity.isDead();
    }

    public void checkCameraAliveStatus() {
        if (!isCameraAlive()) {
            if (cameraWatcher != null) {
            }
            if (isMoving()) {
                stopCamera();
            }
        }
    }

    public boolean isMoving() {
        return (this.moveRunnable != null && !this.moveRunnable.isCancelled()) ||
                (this.pointMoveRunnable != null && !this.pointMoveRunnable.isCancelled());
    }

    public void stopCamera() {
        if (this.moveRunnable != null && !this.moveRunnable.isCancelled()) {
            moveRunnable.cancel();
        }
        moveRunnable = null;
        if (this.pointMoveRunnable != null && !this.pointMoveRunnable.isCancelled()) {
            pointMoveRunnable.cancel();
        }
        pointMoveRunnable = null;
    }

    public ItemDisplay getCameraEntity() {
        return cameraEntity;
    }

    public void setUpInScene(Scene scene) {
        this.inScene = scene;
    }

    private float getShortestAngleStep(float startYaw, float targetYaw, long time) {
        startYaw = normalizeYaw(startYaw);
        targetYaw = normalizeYaw(targetYaw);

        float diff = targetYaw - startYaw;

        if (diff > 180) {
            diff -= 360;
        } else if (diff < -180) {
            diff += 360;
        }

        return diff / time;
    }
}
