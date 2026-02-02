package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;

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

    private final Map<Integer, ContentRpgEntity> contentMobMap = new HashMap<>();
    private final Map<UUID, RpgEntity> rpgEntityContainerMap = new HashMap<>();

    public void registerContentMob(int id, ContentRpgEntity contentMob) {
        contentMobMap.put(id, contentMob);
    }

    public void registerRpgEntity(UUID entityID, RpgEntity rpgEntity) {
        rpgEntityContainerMap.put(entityID, rpgEntity);
    }

    public void spawnRpgEntity(String context, int id, Location location) {
        ContentRpgEntity cte = contentMobMap.get(id);
        if (cte == null) {
            throw new IllegalArgumentException("This ID not exists.");
        }
        LivingEntity contentMob = (LivingEntity) location.getWorld().spawnEntity(location, cte.getType());
        Main.tweakManager.getContextManager().moveToContext(contentMob, context);
        contentMob.setCustomName(cte.getName());
        contentMob.setCustomNameVisible(true);
        contentMob.setMaxHealth(cte.getMaxHealth());
        contentMob.setHealth(cte.getMaxHealth());
        contentMob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(cte.getMovementSpeed());
        contentMob.getEquipment().setItemInMainHand(cte.getHand(), false);
        contentMob.getEquipment().setItemInOffHand(cte.getOffHand(), false);
        contentMob.getEquipment().setHelmet(cte.getHelmet(), false);
        contentMob.getEquipment().setChestplate(cte.getChestPlate(), false);
        contentMob.getEquipment().setLeggings(cte.getLeggings(), false);
        contentMob.getEquipment().setBoots(cte.getBoots(), false);
        contentMob.setCanPickupItems(false);
        contentMob.setRemoveWhenFarAway(true);
        registerRpgEntity(contentMob.getUniqueId(), new RpgMob(contentMob.getUniqueId(), cte));
    }
}
