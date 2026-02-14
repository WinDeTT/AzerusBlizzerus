package org.windett.azerusBlizzerus.rpg.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.windett.azerusBlizzerus.Main;

public class RpgItemStack {

    public static ItemStack rpgItemStack(int id) {
        final RpgItem rpgItem = Main.rpgSystemManager.getRpgItemManager().getRpgItem(id);
        if (rpgItem == null) return null;
        ItemStack item = ItemStack.of(rpgItem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                Component.text(rpgItem.getDisplayName()).color(rpgItem.getItemRarity().getColor()).decoration(TextDecoration.ITALIC, false)
        );



        item.setItemMeta(meta);
        return item;
    }
}
