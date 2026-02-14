package org.windett.azerusBlizzerus.rpg.entity.pathfinder.goalSelector;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.entity.Entity;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgMob;

import java.util.EnumSet;

public class CustomRpgMeleeAttackGoal extends Goal {
    protected final PathfinderMob mob;
    protected RpgMob rpgMob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval;
    private final double attackRange;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

    public CustomRpgMeleeAttackGoal(PathfinderMob mob, double speedModifier, int attackInterval, double attackRange, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.attackInterval = attackInterval;
        this.attackRange = attackRange;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        this.rpgMob = (RpgMob) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(this.mob.getBukkitEntity());
    }

    public boolean canUse() {
        if (rpgMob == null) return false;
        long gameTime = this.mob.level().getGameTime();
        if (gameTime - this.lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS) {
            return false;
        } else {
            this.lastCanUseCheck = gameTime;
            LivingEntity target = getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().createPath(target, 0);
                return this.path != null || isWithinCustomAttackRange(target);
            }
        }
    }

    public boolean canContinueToUse() {
        if (rpgMob == null) return false;
        LivingEntity target = getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else {
            boolean undoneNavigate;
            if (!this.followingTargetEvenIfNotSeen) {
                undoneNavigate = !this.mob.getNavigation().isDone();
            } else {
                label43: {
                    if (this.mob.isWithinHome(target.blockPosition())) {
                        if (!(target instanceof Player player)) {
                            break label43;
                        }

                        if (!player.isSpectator() && !player.isCreative()) {
                            break label43;
                        }
                    }

                    undoneNavigate = false;
                    return undoneNavigate;
                }

                undoneNavigate = true;
            }

            return undoneNavigate;
        }
    }

    public void start() {
        if (rpgMob == null) return;
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    public void stop() {
        if (rpgMob == null) return;
        LivingEntity target = getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setLastHurtByMob(null);
        }

        this.mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (rpgMob == null) return;
        LivingEntity target = getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(target)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = target.getX();
                this.pathedTargetY = target.getY();
                this.pathedTargetZ = target.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.distanceToSqr(target);
                if (d > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(target, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(target);
        }

    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (rpgMob == null) return;
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            if (!this.mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) return;
            rpgMob.attack(rpgMob.getTarget(), rpgMob.getDamageStats().getPhysicalDamage());
        }

    }

    protected LivingEntity getTarget() {
        if (rpgMob == null) return null;
        Entity target = rpgMob.getTargetEntity();

        if (target instanceof org.bukkit.entity.Player player) {
            return ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
        }
        return null;
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackInterval);
    }


    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity entity) {
        return this.isTimeToAttack() && isWithinCustomAttackRange(entity) && this.mob.getSensing().hasLineOfSight(entity);
    }

    protected boolean isWithinCustomAttackRange(LivingEntity entity) {

        double distance = this.mob.distanceToSqr(entity);
        double attackRange = this.attackRange; // 3 блока
        double attackRangeSquared = attackRange * attackRange;

        return distance <= attackRangeSquared;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(attackInterval);
    }
}
