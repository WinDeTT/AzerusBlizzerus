package org.windett.azerusBlizzerus.rpg.player.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.windett.azerusBlizzerus.rpg.inventories.RpgInventoryHolders;

import java.util.HashMap;
import java.util.Map;

public class PlayerStorageData {


    private int storageLevel;
    private final Map<Integer, Inventory> storagePages;

    public PlayerStorageData() {
        this.storageLevel = 0;
        this.storagePages = new HashMap<>();
        expandStorage();
    }

    public void expandStorage() {
        this.storageLevel++;
        this.storagePages.put(this.storageLevel, next(this.storageLevel));
    }

    public Inventory getStoragePage(int page) {
        if (page < 1) return null;
        return this.storagePages.getOrDefault(page, null);
    }

    public Inventory next(int storageLevel) {
        return Bukkit.createInventory(RpgInventoryHolders.storageHolder, 54, Component.text(String.format("Хранилище (%d стр.)", storageLevel)));
    }
}
