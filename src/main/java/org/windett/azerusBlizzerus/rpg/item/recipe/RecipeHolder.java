package org.windett.azerusBlizzerus.rpg.item.recipe;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class RecipeHolder implements InventoryHolder {

    private final Inventory recInventory;

    public RecipeHolder() {
        this.recInventory = Bukkit.createInventory(this, 54, Component.text("Рецепт"));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return recInventory;
    }
}
