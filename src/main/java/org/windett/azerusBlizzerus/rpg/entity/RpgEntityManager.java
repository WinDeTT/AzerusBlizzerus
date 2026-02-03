package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.content.ContentRpgSpawner;
import org.windett.azerusBlizzerus.events.custom.rpg.entity.mob.RpgMobSpawnEvent;

import java.util.HashMap;
import java.util.Map;
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

    public Entity spawnRpgEntity(String context, int id, Location location, ContentRpgSpawner spawner) {
        ContentRpgEntity cte = contentMobMap.get(id);
        if (cte == null) {
            throw new IllegalArgumentException("This ID not exists.");
        }
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, cte.getType());
        Main.tweakManager.getContextManager().moveToContext(mob, context);
        mob.setCustomName(cte.getName());
        mob.setCustomNameVisible(true);
        mob.setMaxHealth(cte.getMaxHealth());
        mob.setHealth(cte.getMaxHealth());
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(cte.getMovementSpeed());
        mob.getEquipment().setItemInMainHand(cte.getHand(), false);
        mob.getEquipment().setItemInOffHand(cte.getOffHand(), false);
        mob.getEquipment().setHelmet(cte.getHelmet(), false);
        mob.getEquipment().setChestplate(cte.getChestPlate(), false);
        mob.getEquipment().setLeggings(cte.getLeggings(), false);
        mob.getEquipment().setBoots(cte.getBoots(), false);
        mob.setCanPickupItems(false);
        mob.setPersistent(false);
        if (mob instanceof Ageable) {
            boolean isBaby = cte.isBaby();
            if (isBaby) {
                ((Ageable) mob).setBaby();
            }
            else {
                ((Ageable) mob).setAdult();
            }
        }
        final RpgMob rpgMob = new RpgMob(mob.getUniqueId(), cte, spawner);
        registerRpgEntity(mob.getUniqueId(), rpgMob);
        Bukkit.getPluginManager().callEvent(new RpgMobSpawnEvent(rpgMob));
        return mob;
    }

    public RpgEntity asRpgMob(Entity entity) {
        return rpgEntityContainerMap.get(entity.getUniqueId());
    }
}
