package org.windett.azerusBlizzerus.utils.pathRecorder;

import io.papermc.paper.entity.LookAnchor;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ScriptedMovement {

    private Location startLoc = null;
    private final List<PathPositionData> positionDataList = new ArrayList<>();
    private int currentIndex = 0;
    private enum Mode{
        RECORDING,
        REPLAYING;
    }
    private LivingEntity rubberTarget;
    private boolean lookRubberTargetWhenFar = false;
    private Pair<Double, Double> rubberNearDistance = Pair.of(0.0, 0.0);
    private Pair<Double, Double> rubberFarDistance = Pair.of(1000.0,1000.0);
    public enum RubberFar{
        SIMPLE(2),
        VERY(3);

        public final int delay;

        RubberFar(int delay) {
            this.delay = delay;
        }
    }
    private Mode pathMode = Mode.RECORDING;

    public BukkitTask getRunnable() {
        return runnable;
    }

    private BukkitTask runnable;

    public ScriptedMovement() {
    }

    public void setStartLocation(Location location) {
        this.startLoc = location;
    }

    /*
    Начать запись движения (по-тиковая запись Vector offset и yaw/pitch offset)
     */
    public void runStartRecord(Player target) {
        runStartRecord(target, 0);
    }
    public void runStartRecord(Player target, int startIndex) {
        if (this.runnable != null) return;
        if (startIndex < 0) startIndex = 0;
        if (target == null) return;
        this.pathMode = Mode.RECORDING;
        this.currentIndex = startIndex;
        target.setRotation(target.getYaw(),0F);
        this.startLoc = target.getLocation().clone();
        if (this.currentIndex == 0) {
            this.positionDataList.clear();
        }
        else {
            if (this.currentIndex > positionDataList.size()) this.currentIndex = positionDataList.size();
            if (this.currentIndex < positionDataList.size()) {
                positionDataList.subList(this.currentIndex, positionDataList.size()).clear();
            }
        }
        runnable = new BukkitRunnable() {
            Location currentLoc;
            public void run() {
                if (!target.isOnline()) {
                    clearAll();
                }
                if (target.isDead()) {
                    stop(target, false);
                }
                translateTickForPlayer(target, pathMode);
                currentLoc = target.getLocation();
                Vector worldOffset = currentLoc.toVector().subtract(startLoc.toVector());
                Vector localOffset = ScriptMoveManager.rotateVector(worldOffset, -startLoc.getYaw(), -startLoc.getPitch());
                float relativeYaw = currentLoc.getYaw() - startLoc.getYaw();
                float relativePitch = currentLoc.getPitch() - startLoc.getPitch();

                positionDataList.add(new PathPositionData(localOffset, Pair.of(relativeYaw, relativePitch)));
                currentIndex++;
            }
        }.runTaskTimer(Main.instance, 0L, 1L);
    }

    /*
    Начать воспроизведение пути (по-тиковая телепортация на следующую локацию относительно смещения по Vector и Rotation)
    Флаг UseOriginalStartPosition учтёт стартовую позицию, установленную при записи. Если она null, то запустит исходя из текущей.
     */
    public void runReplayRecording(Entity target, int startIndex, Boolean useOriginalStartPosition, Runnable executeAtEnd) {
        if (this.runnable != null) return;
        if (startIndex < 0) startIndex = 0;
        if (target == null) return;
        if (!target.isValid()) return;
        this.pathMode = Mode.REPLAYING;
        this.currentIndex = startIndex;
        if (this.currentIndex >= positionDataList.size()) currentIndex = 0;
        if (target instanceof LivingEntity && (!(target instanceof Player))) {
            ((LivingEntity) target).setAI(false);
        }
        final Location startLoc;
        if (useOriginalStartPosition) startLoc = this.getStartLocation().clone();
        else {
            startLoc = target.getLocation().clone();
        }

        CraftEntity craftTarget = (CraftEntity) target;
        ServerLevel serverLevel = (ServerLevel) craftTarget.getHandle().level();

        runnable =  new BukkitRunnable() {
            int tickDelay = 1;
            int requestDelay = 1;
            Vector worldOffset;
            Location current;
            Location moveLoc;
            double dist;
            int newIndex;
            boolean ignoreTarget = false;
            public void run() {
                newIndex = currentIndex;
                requestDelay = 1;
                if (target instanceof Player) translateTickForPlayer((Player) target, pathMode);
                if (rubberTarget != null) {
                    if (!rubberTarget.isValid()) rubberTarget = null;
                    else {
                        if (rubberTarget instanceof Player && ((Player) rubberTarget).getGameMode() == GameMode.SPECTATOR) ignoreTarget = true;
                        else ignoreTarget = false;

                        if (!ignoreTarget) {
                            dist = target.getLocation().distanceSquared(rubberTarget.getLocation());
                            if (dist >= rubberFarDistance.first() * rubberFarDistance.first() && dist < rubberFarDistance.second() * rubberFarDistance.second()) {
                                requestDelay = RubberFar.SIMPLE.delay;
                            } else if (dist >= rubberFarDistance.second() * rubberFarDistance.second()) {
                                requestDelay = RubberFar.VERY.delay;
                            }

                            if (dist <= rubberNearDistance.first() * rubberNearDistance.first() && (dist > rubberNearDistance.second() * rubberNearDistance.second())) {
                                newIndex += 2;
                            } else if (dist < rubberNearDistance.second() * rubberNearDistance.second()) {
                                newIndex += 3;
                            } else newIndex++;
                        }
                        else newIndex++;
                    }
                }
                else newIndex++;

                if (tickDelay >= requestDelay) {
                    current = target.getLocation();
                    worldOffset = ScriptMoveManager.rotateVector(positionDataList.get(currentIndex).getLocOffset(), startLoc.getYaw(), startLoc.getPitch());
                    moveLoc = startLoc.clone().add(worldOffset);
                    moveLoc.setYaw(startLoc.getYaw() + positionDataList.get(currentIndex).getRotation().first());
                    moveLoc.setPitch(startLoc.getPitch() + positionDataList.get(currentIndex).getRotation().second());
                    craftTarget.getHandle().teleportTo(moveLoc.getX(), moveLoc.getY(), moveLoc.getZ());
                    craftTarget.getHandle().setRot(moveLoc.getYaw(), moveLoc.getPitch());
                    if (requestDelay > 1 && (rubberTarget != null && rubberTarget.isValid())) {
                        if (lookRubberTargetWhenFar) target.lookAt(rubberTarget.getLocation(), LookAnchor.EYES);
                    }
                    tickDelay = 1;
                    currentIndex = newIndex;
                }
                else {
                    tickDelay++;
                }

                if (currentIndex >= positionDataList.size()) {
                    if (target instanceof LivingEntity && (!(target instanceof Player))) {
                        ((LivingEntity) target).setAI(true);
                    }
                    stop(target, true);
                    if (executeAtEnd != null) {
                        executeAtEnd.run();
                    }
                }

            }
        }.runTaskTimer(Main.instance, 0L, 1L);
    }

    /*
    Останавливает Runnable, отвечающий за record или replay.
     */
    public void stop(Entity entity, boolean returnAi) {
        if (runnable == null) return;
        if (!runnable.isCancelled()) runnable.cancel();
        runnable = null;

        if (returnAi) {
            if (entity instanceof LivingEntity && (!(entity instanceof Player))) ((LivingEntity) entity).setAI(true);
        }
        if (entity instanceof Player) {
            if (pathMode == Mode.RECORDING) ((Player) entity).sendActionBar(TextUtils.formatColors("&aЗапись завершена!"));
            else ((Player) entity).sendActionBar(TextUtils.formatColors("&bВоспроизведение завершено!"));
        }
    }

    /*
    Установить сущность, по-отношению к которому будет действовать система Rubber Banding.
    Rubber Banding - система, поддерживающая динамику скорости скриптового движения.
    Если сущность не установлена, система применена не будет.
     */
    public void setRubberTarget(LivingEntity entity, boolean lookRubberTargetWhenFar) {
        this.rubberTarget = entity;
        this.lookRubberTargetWhenFar = lookRubberTargetWhenFar;
    }
    /*
    Удалить цель, по-отношению к которой применяется система Rubber Banding.
     */
    public void removeRubberTarget() {
        if (rubberTarget != null) this.rubberTarget = null;
        this.lookRubberTargetWhenFar = false;
    }
    /*
    Установит близкую и очень близкую дистанцию сущности Rubber Target по отношению к сущности, на которой воспроизводится путь.
    Когда Rubber Target подбегает близко к сущности на пути, то она ускоряется.
     */
    public void setRubberNearDistance(double simpleNear, double veryNear) {
        this.rubberNearDistance = Pair.of(simpleNear, veryNear);
    }
    /*
    Установит дальнюю и очень дальнюю дистанцию сущности Rubber Target по отношению к сущности, на которой воспроизводится путь.
    Когда Rubber Target отстаёт от сущности на пути, то она замедляется.
     */
    public void setRubberFarDistance(double simpleFar, double veryFar) {
        this.rubberFarDistance = Pair.of(simpleFar, veryFar);
    }

    public Location getStartLocation() {return this.startLoc;}

    private void translateTickForPlayer(Player player, Mode mode) {
        if (mode == Mode.RECORDING) player.sendActionBar(TextUtils.formatColors("&aИдёт запись: &b" + currentIndex));
        else player.sendActionBar(TextUtils.formatColors("&6Воспроизведение: &b" + currentIndex));
    }


    public void clearAll() {
        if (this.runnable != null && !this.runnable.isCancelled()) this.runnable.cancel();
        this.runnable = null;
        this.positionDataList.clear();
    }

    public boolean isEmpty() {
        return this.positionDataList.isEmpty();
    }

    public List<PathPositionData> getPathList() {return this.positionDataList;}

}
