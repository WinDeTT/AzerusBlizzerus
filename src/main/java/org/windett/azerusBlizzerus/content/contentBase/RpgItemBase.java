package org.windett.azerusBlizzerus.content.contentBase;

import org.bukkit.Material;
import org.windett.azerusBlizzerus.rpg.item.RpgItem;

public class RpgItemBase {

    public RpgItemBase() {
        RpgItem rpgItem = new RpgItem.Builder()
                .id(1)
                .material(Material.EMERALD)
                .type(RpgItem.ItemType.RESOURCE)
                .rarity(RpgItem.ItemRarity.COMMON)
                .build();
    }
}
