package org.windett.azerusBlizzerus.rpg.item;

import org.bukkit.Material;
import org.windett.azerusBlizzerus.rpg.item.enums.WeaponEnum;

public class RpgItemBase {

    private RpgItem rpgItem;

    public RpgItemBase() { // инициализировать в Main
        rpgItem = new RpgItem.Builder()
                .id(1)
                .material(Material.EMERALD)
                .type(RpgItem.ItemType.RESOURCE)
                .rarity(RpgItem.ItemRarity.COMMON)
                .build();
        rpgItem = new RpgItem.Builder()
                .id(2)
                .material(Material.STONE_SWORD)
                .type(RpgItem.ItemType.WEAPON)
                .weaponType(WeaponEnum.WeaponType.MELEE)
                .weaponSubType(WeaponEnum.WeaponSubType.SWORD)
                .build();
    }
}
