package org.windett.azerusBlizzerus.content.contentBase;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;

public class RpgMobBase {

    public RpgMobBase() {
    }

    public void init() {
        ContentRpgEntity cte = new ContentRpgEntity.Builder()
                .id(1)
                .name("Тестовый моб")
                .type(EntityType.ZOMBIE)
                .specific(ContentRpgEntity.Specific.SIMPLE)
                .level(1)
                .xpLoot(0.075)
                .maxHealth(15.0)
                .physicalDamage(1.0)
                .physicalDefence(1.0)
                .hand(Material.STONE_SWORD)
                .offHand(Material.BOW)
                .helmet(Material.LEATHER_HELMET)
                .speed(0.35F)
                .baby(false)
                .build();
    }
}
