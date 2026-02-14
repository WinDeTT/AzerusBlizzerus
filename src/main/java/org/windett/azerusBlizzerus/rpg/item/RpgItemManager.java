package org.windett.azerusBlizzerus.rpg.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.css.RGBColor;
import org.windett.azerusBlizzerus.rpg.item.recipe.ItemRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgItemManager {

    public Map<Integer, RpgItem> getRpgItemMap() {
        return rpgItemMap;
    }

    public Map<Integer, ItemRecipe> getItemRecipeMap() {
        return itemRecipeMap;
    }

    private final Map<Integer, RpgItem> rpgItemMap = new HashMap<>();
    private final Map<Integer, ItemRecipe> itemRecipeMap = new HashMap<>();
    private ItemRecipe recipe;

    public RpgItemManager() {
    }

    public void registerRpgItem(RpgItem rpgItem) {
        rpgItemMap.put(rpgItem.getId(), rpgItem);
    }

    public RpgItem getRpgItem(int id) {
        return rpgItemMap.get(id);
    }


    public void openRecipe(Player player, int recipeId) {
        ItemRecipe recipe = itemRecipeMap.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("This recipe is not exists!");
        }
        Inventory defaultRecipeInventory = recipe.getRecipeInventory();
        if (defaultRecipeInventory == null) {
            throw new IllegalArgumentException("[NULL] Problem with recipe inventory! Please, tell administrator about this.");
        }
        if (defaultRecipeInventory.isEmpty()) {
            throw new IllegalArgumentException("[EMPTY] Problem with recipe inventory! Please, tell administrator about this.");
        }
        ItemStack[] contents = defaultRecipeInventory.getStorageContents();
        InventoryHolder holder = defaultRecipeInventory.getHolder();
        int size = defaultRecipeInventory.getSize();
        Inventory openedToPlayer = Bukkit.createInventory(holder, size, recipe.getRecipeTitle());
        openedToPlayer.setStorageContents(contents.clone());
        player.openInventory(openedToPlayer);
    }

    public void registerRecipes() {
        recipe = new ItemRecipe.Builder()
                .setResultItem(ItemStack.of(Material.LEATHER))
                .setIngredients(List.of(ItemStack.of(Material.COPPER_INGOT), ItemStack.of(Material.MAGMA_CREAM)))
                .build(1);
        recipe = new ItemRecipe.Builder()
                .setResultItem(ItemStack.of(Material.STONE_AXE, 1))
                .setIngredients(List.of(ItemStack.of(Material.STICK), ItemStack.of(Material.STONE, 3), ItemStack.of(Material.LEATHER, 2)))
                .build(2);
    }
}
