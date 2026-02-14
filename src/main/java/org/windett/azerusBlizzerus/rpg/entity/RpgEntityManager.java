package org.windett.azerusBlizzerus.rpg.entity;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.*;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.rpg.entity.pathfinder.goalSelector.CustomRpgMeleeAttackGoal;
import org.windett.azerusBlizzerus.rpg.entity.pathfinder.targetSelector.CustomRpgTargetGoal;
import org.windett.azerusBlizzerus.rpg.entity.spawner.ContentRpgSpawner;
import org.windett.azerusBlizzerus.events.custom.rpg.entity.mob.RpgMobSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RpgEntityManager {

    public Map<Integer, ContentRpgEntity> getContentMobMap() {
        return contentMobMap;
    }
    public Map<UUID, RpgEntity> getRpgEntityContainerMap() {
        return rpgEntityContainerMap;
    }
    public Map<UUID, ContentRpgSpawner> getRpgMobSpawnerMap() {
        return rpgMobSpawnerMap;
    }

    private final Map<Integer, ContentRpgEntity> contentMobMap = new HashMap<>();
    private final Map<UUID, RpgEntity> rpgEntityContainerMap = new HashMap<>();
    private final Map<UUID, ContentRpgSpawner> rpgMobSpawnerMap = new HashMap<>();

    public void registerContentMob(int id, ContentRpgEntity contentMob) {
        contentMobMap.put(id, contentMob);
    }

    public void registerRpgEntity(UUID entityID, RpgEntity rpgEntity) {
        rpgEntityContainerMap.put(entityID, rpgEntity);
    }
    public void unregisterRpgEntity(RpgEntity rpgEntity) {
        rpgEntityContainerMap.remove(rpgEntity.getUniqueId());
    }

    public Entity spawnRpgEntity(String context, int id, Location location, ContentRpgSpawner spawner) {
        ContentRpgEntity cte = contentMobMap.get(id);
        if (cte == null) {
            throw new IllegalArgumentException("This ID not exists.");
        }
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, cte.getType());
        Main.tweakManager.getContextManager().moveToContext(mob, context);
        mob.setCustomName(cte.getName());
        mob.setCustomNameVisible(true);
        mob.setSilent(true);
        mob.setMaxHealth(cte.getMaxHealth());
        mob.setHealth(cte.getMaxHealth());
        Objects.requireNonNull(mob.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(cte.getMovementSpeed());
        Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(cte.getHand(), false);
        mob.getEquipment().setItemInOffHand(cte.getOffHand(), false);
        mob.getEquipment().setHelmet(cte.getHelmet(), false);
        mob.getEquipment().setChestplate(cte.getChestPlate(), false);
        mob.getEquipment().setLeggings(cte.getLeggings(), false);
        mob.getEquipment().setBoots(cte.getBoots(), false);
        mob.setCanPickupItems(false);
        mob.setPersistent(false);
        Vehicle vehicle = (Vehicle) mob.getVehicle();
        if (vehicle != null && vehicle.isValid()) {
            if (vehicle instanceof Chicken) {
                vehicle.remove();
            }
        }

        if (mob instanceof Ageable) {
            boolean isBaby = cte.isBaby();
            if (isBaby) {
                ((Ageable) mob).setBaby();
            }
            else {
                ((Ageable) mob).setAdult();
            }
        }

        CraftMob craftMob = (CraftMob) mob;
        clearPathfinder(mob);
        GoalSelector goalSelector = craftMob.getHandle().goalSelector;
        goalSelector.addGoal(0, new FloatGoal(craftMob.getHandle()));
        goalSelector.addGoal(1, new RandomStrollGoal((PathfinderMob) craftMob.getHandle(), 1.0));
        goalSelector.addGoal(2, new RandomLookAroundGoal(craftMob.getHandle()));


        final RpgMob rpgMob = new RpgMob(mob.getUniqueId(), cte, spawner);
        registerRpgEntity(mob.getUniqueId(), rpgMob);
        Bukkit.getPluginManager().callEvent(new RpgMobSpawnEvent(rpgMob));
        return mob;
    }

    public void clearPathfinder(Entity entity) {
        CraftMob cm = (CraftMob) entity;
        cm.getHandle().goalSelector.removeAllGoals(goal -> true);
        cm.getHandle().targetSelector.removeAllGoals(goal -> true);
    }

    public RpgEntity asRpgMob(Entity entity) {
        return rpgEntityContainerMap.get(entity.getUniqueId());
    }
}
