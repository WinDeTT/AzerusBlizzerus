package org.windett.azerusBlizzerus.rpg.item.recipe;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.windett.azerusBlizzerus.Main;

import java.util.List;
import java.util.Map;


public class ItemRecipe {

    private static final Map<Integer, Integer> ingredientSlotMap = Map.of(
            1,10,
            2,12,
            3,14
    );



    public Inventory getRecipeInventory() {
        return recipeInventory;
    }
    public ItemStack getResultItem() {
        return resultItem;
    }
    public ItemStack[] getIngredients() {
        return ingredients;
    }
    public Component getRecipeTitle() {
        return recipeTitle;
    }

    private final Inventory recipeInventory;
    private final ItemStack resultItem;
    private final ItemStack[] ingredients;
    private final Component recipeTitle;



    public ItemRecipe(int recipeId, ItemStack resultItem, Builder builder) {
        this.resultItem = builder.resultItem;
        this.ingredients = builder.ingredients;
        this.recipeTitle = Component.text("Рецепт: " + this.resultItem.getType().toString().toUpperCase());
        this.recipeInventory = Bukkit.createInventory(new RecipeHolder(), 54, recipeTitle);
        for (int slot = 0; slot < this.recipeInventory.getSize(); slot++) {
            if (slot <= 26) {
                this.recipeInventory.setItem(slot, filler(Material.BLUE_STAINED_GLASS_PANE));
            }
            else this.recipeInventory.setItem(slot, filler(Material.RED_STAINED_GLASS_PANE));
        }
        final int RESULT_SLOT = 16;
        final int CONFIRM_SLOT = 43;
        recipeInventory.setItem(RESULT_SLOT, resultItem);
        recipeInventory.setItem(CONFIRM_SLOT, new ItemStack(Material.EMERALD_BLOCK));
        int ingredientNumber = 1;
        while (ingredientNumber <= this.ingredients.length) {
            int slot = ingredientSlotMap.get(ingredientNumber);
            recipeInventory.setItem(ingredientSlotMap.get(ingredientNumber), ingredients[ingredientNumber - 1]);
            recipeInventory.setItem(slot + 27, null);
            ingredientNumber++;
        }

        Main.tweakManager.getRpgItemManager().getItemRecipeMap().put(recipeId, this);
    }

    public static class Builder {
        private ItemStack resultItem = new ItemStack(Material.STONE_SWORD);
        private ItemStack[] ingredients = new ItemStack[] {
                ItemStack.of(Material.STICK),
                ItemStack.of(Material.STONE),
                ItemStack.of(Material.STONE)
        };

        public Builder setResultItem(ItemStack item) {
            this.resultItem = item;
            return this;
        }
        public Builder setIngredients(List<ItemStack> items) {
            if (items.size() > 3) {
                items = items.subList(0,3);
            }
            this.ingredients = items.toArray(new ItemStack[0]);
            return this;
        }
        public ItemRecipe build(int recipeId) {
            return new ItemRecipe(recipeId, this.resultItem, this);
        }
    }

    private ItemStack filler(Material material) {
        final ItemStack fillerStack = new ItemStack(material);
        final ItemMeta meta = fillerStack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Main.instance, "fillerItem"), PersistentDataType.BOOLEAN, true);
        meta.setHideTooltip(true);
        return fillerStack;
    }
}
