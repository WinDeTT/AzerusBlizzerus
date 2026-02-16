package org.windett.azerusBlizzerus.rpg.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.windett.azerusBlizzerus.Main;

import java.util.ArrayList;
import java.util.List;

public class RpgItemStack {

    private static final NamespacedKey itemIdKey = new NamespacedKey(Main.instance, "id");
    private static final NamespacedKey itemRarityKey = new NamespacedKey(Main.instance, "rarity");

    public static ItemStack rpgItemStack(int id) {
        final RpgItem rpgItem = Main.rpgSystemManager.getRpgItemManager().getRpgItem(id);
        if (rpgItem == null) return null;
        ItemStack item = ItemStack.of(rpgItem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                Component.text(rpgItem.getDisplayName()).color(rpgItem.getItemRarity().getColor()).decoration(TextDecoration.ITALIC, false)
        );

        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(itemIdKey, PersistentDataType.INTEGER, rpgItem.getId());
        pdc.set(itemRarityKey, PersistentDataType.STRING, rpgItem.getItemRarity().name());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());


        Component debugIdKey = Component.text("id: " + rpgItem.getId()).decoration(TextDecoration.ITALIC, false).color(TextColor.color(48, 46, 50));
        lore.add(debugIdKey);
        meta.lore(lore);
        item.setItemMeta(meta);



        item.setItemMeta(meta);
        return item;
    }



    public static NamespacedKey getItemIdKey() {
        return itemIdKey;
    }
}
