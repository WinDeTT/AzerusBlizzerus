package org.windett.azerusBlizzerus.rpg.entity.pathfinder.targetSelector;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgMob;
import org.windett.azerusBlizzerus.rpg.entity.RpgPlayer;
import org.bukkit.entity.Entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class CustomRpgTargetGoal extends TargetGoal {
    protected final Mob mob;
    protected RpgMob rpgMob;
    private final boolean onlyDamagers;
    private final double searchRange;
    private int checkCooldown = 0;

    // Убираем наследование target от родителя, работаем только через RpgMob
    private LivingEntity currentNmsTarget = null;

    public CustomRpgTargetGoal(Mob mob, boolean onlyDamagers, double searchRange) {
        super(mob, false);
        this.mob = mob;
        this.onlyDamagers = onlyDamagers;
        this.searchRange = searchRange;
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.rpgMob = (RpgMob) Main.rpgSystemManager.getRpgEntityManager()
                .asRpgMob(this.mob.getBukkitEntity());
    }

    @Override
    public boolean canUse() {
        if (rpgMob == null || !rpgMob.isValid()) {
            return false;
        }

        // Проверяем таймер
        if (checkCooldown > 0) {
            checkCooldown--;
            return false;
        }
        checkCooldown = 20;

        // Получаем текущую цель из RpgMob
        RpgPlayer currentRpgTarget = rpgMob.getTarget();

        // Если есть цель - проверяем её валидность
        if (currentRpgTarget != null && currentRpgTarget.isValid()) {
            // Проверяем, жива ли цель и в радиусе
            if (isRpgTargetValid(currentRpgTarget)) {
                // Цель валидна - обновляем currentNmsTarget
                updateNmsTargetFromRpgTarget(currentRpgTarget);
                return true;
            } else {
                // Цель невалидна - очищаем
                rpgMob.setTarget(null);
                currentNmsTarget = null;
            }
        }

        // Ищем новую цель
        RpgPlayer newTarget = findNewRpgTarget();
        if (newTarget != null && newTarget.isValid()) {
            rpgMob.setTarget(newTarget);
            updateNmsTargetFromRpgTarget(newTarget);
            return true;
        }

        return false;
    }

    private void updateNmsTargetFromRpgTarget(RpgPlayer rpgTarget) {
        Entity bukkitEntity = rpgTarget.asBukkitEntity();
        if (bukkitEntity instanceof org.bukkit.entity.Player player) {
            this.currentNmsTarget = ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
        } else {
            this.currentNmsTarget = null;
        }
    }

    private boolean isRpgTargetValid(RpgPlayer rpgTarget) {
        if (!rpgTarget.isValid()) return false;

        // Проверка дистанции
        if (rpgMob.getLocation() != null && rpgTarget.getLocation() != null) {
            double distance = rpgMob.getLocation().distance(rpgTarget.getLocation());
            if (distance > searchRange) {
                return false;
            }
        }

        // Проверка видимости
        Entity bukkitEntity = rpgTarget.asBukkitEntity();
        if (!(bukkitEntity instanceof org.bukkit.entity.Player player)) {
            return false;
        }

        if (!this.mob.getSensing().hasLineOfSight(
                ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle())) {
            return false;
        }

        // Если режим "только дамагеры" - проверяем трекер
        if (onlyDamagers) {
            if (rpgMob.getDamagersTracker() != null) {
                Map<RpgPlayer, Double> damageMap = rpgMob.getDamagersTracker().getDamageMap();
                if (!damageMap.containsKey(rpgTarget)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private RpgPlayer findNewRpgTarget() {
        if (onlyDamagers) {
            return findTargetFromDamagers();
        } else {
            return findNearestRpgPlayer();
        }
    }

    private RpgPlayer findTargetFromDamagers() {
        if (rpgMob.getDamagersTracker() != null) {
            Map<RpgPlayer, Double> damageMap = rpgMob.getDamagersTracker().getDamageMap();

            if (!damageMap.isEmpty()) {
                return damageMap.entrySet().stream()
                        .max((a, b) -> Double.compare(a.getValue(), b.getValue()))
                        .map(e -> e.getKey())
                        .orElse(null);
            }
        }
        return null;
    }

    private RpgPlayer findNearestRpgPlayer() {
        if (!rpgMob.isValid()) return null;

        List<RpgPlayer> nearbyRpgPlayers = rpgMob.getNearbyPlayers(35);
        if (nearbyRpgPlayers == null || nearbyRpgPlayers.isEmpty()) {
            return null;
        }

        RpgPlayer nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        org.bukkit.Location mobLocation = rpgMob.getLocation();

        for (RpgPlayer rpgPlayer : nearbyRpgPlayers) {
            if (!rpgPlayer.isValid()) continue;

            if (rpgPlayer.getLocation() == null) continue;

            double distance = rpgPlayer.getLocation().distance(mobLocation);
            if (distance > searchRange) continue;

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = rpgPlayer;
            }
        }

        return nearest;
    }

    @Override
    public boolean canContinueToUse() {
        if (rpgMob == null || !rpgMob.isValid()) {
            return false;
        }

        RpgPlayer currentTarget = rpgMob.getTarget();
        if (currentTarget == null || !currentTarget.isValid()) {
            return false;
        }

        return isRpgTargetValid(currentTarget);
    }

    @Override
    public void start() {
        // Не используем mob.setTarget() - работаем только через RpgMob
        // currentNmsTarget уже установлен в canUse()
        super.start();
    }

    @Override
    public void stop() {
        // Очищаем только RpgMob цель
        rpgMob.setTarget(null);
        currentNmsTarget = null;
        super.stop();
    }

    // Метод для получения NMS цели (если нужно для других целей)
    public LivingEntity getCurrentNmsTarget() {
        return currentNmsTarget;
    }

    // Метод для принудительной установки цели
    public void forceSetRpgTarget(RpgPlayer rpgTarget) {
        if (rpgTarget != null && rpgTarget.isValid()) {
            rpgMob.setTarget(rpgTarget);
            updateNmsTargetFromRpgTarget(rpgTarget);
        }
    }
}