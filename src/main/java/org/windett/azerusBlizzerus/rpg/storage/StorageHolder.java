package org.windett.azerusBlizzerus.rpg.storage;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class StorageHolder implements InventoryHolder {
    private final Inventory storageInventory;

    public StorageHolder() {
        this.storageInventory = Bukkit.createInventory(this, 54, Component.text("Хранилище"));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return storageInventory;
    }

    public StorageHolder getHolder() {
        return this;
    }
}
