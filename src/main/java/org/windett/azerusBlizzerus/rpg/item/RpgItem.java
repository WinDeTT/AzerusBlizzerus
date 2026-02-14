package org.windett.azerusBlizzerus.rpg.item;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.item.enums.WeaponEnum;

public class RpgItem {

    private final int id;
    String displayName;
    private Material material;
    private ItemType itemType;
    private ItemRarity itemRarity;
    private int requirementLevel;
    private WeaponEnum.WeaponType weaponType;
    private WeaponEnum.WeaponSubType weaponSubType;

    public RpgItem(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.material = builder.material;
        this.itemType = builder.itemType;
        this.itemRarity = builder.itemRarity;
        this.weaponType = builder.weaponType;
        this.weaponSubType = builder.weaponSubType;

        if (id < 0) return;
        Main.rpgSystemManager.getRpgItemManager().registerRpgItem(this);
    }

    public static class Builder {
        private int id = 0;
        private String displayName = "content.item";
        private Material material = Material.STONE;
        private ItemType itemType = ItemType.RESOURCE;
        private ItemRarity itemRarity = ItemRarity.COMMON;
        private int requirementLevel = 1;
        private WeaponEnum.WeaponType weaponType = WeaponEnum.WeaponType.MELEE;
        private WeaponEnum.WeaponSubType weaponSubType = WeaponEnum.WeaponSubType.SWORD;

        public Builder id(int id) {
            this.id = id;
            return this;
        }
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        public Builder material(Material material) {
            this.material = material;
            return this;
        }
        public Builder type(ItemType type) {
            this.itemType = type;
            return this;
        }
        public Builder rarity(ItemRarity rarity) {
            this.itemRarity = rarity;
            return this;
        }
        public Builder setRequirementLevel(int level) {
            if (level < 1) level = 1;
            this.requirementLevel = level;
            return this;
        }
        public Builder weaponType(WeaponEnum.WeaponType weaponType) {
            this.weaponType = weaponType;
            return this;
        }
        public Builder weaponSubType(WeaponEnum.WeaponSubType weaponSubType) {
            this.weaponSubType = weaponSubType;
            return this;
        }
        public RpgItem build() {
            return new RpgItem(this);
        }
    }


    public enum ItemType {
        RESOURCE("Ресурс"),
        WEAPON("Оружие"),
        ARMOR("Броня"),
        JEWERLY("Бижутерия"),
        CONSUME("Расходник"),
        ENCHANT("Улучшитель");


        public String getDisplayName() {
            return displayName;
        }
        private final String displayName;

        ItemType(String displayName) {
            this.displayName = displayName;
        }
    }
    public enum ItemRarity {
        COMMON(TextColor.color(103, 255, 126), "Обычное", 0),
        UNCOMMON(TextColor.color(108, 191, 255), "Необычное", 1),
        RARE(TextColor.color(18, 113, 103), "Редкое", 2),
        EPIC(TextColor.color(126, 34, 121), "Эпическое", 3),
        LEGENDARY(TextColor.color(171, 89, 17), "Легендарное", 4),
        MYTHIC(TextColor.color(63, 0, 60), "Мифическое", 5),
        ARTEFACT(TextColor.color(72, 0, 0), "Артефакт", 6);


        public TextColor getColor() {
            return color;
        }
        public String getDisplayName() {
            return displayName;
        }
        public int getPriority() {
            return priority;
        }

        private final TextColor color;
        private final String displayName;
        private final int priority;

        ItemRarity(TextColor color, String displayName, int priority) {
            this.color = color;
            this.displayName = displayName;
            this.priority = priority;
        }
    }

    public int getId() {
        return id;
    }
    public String getDisplayName() {
        return displayName;
    }
    public Material getMaterial() {
        return material;
    }
    public ItemRarity getItemRarity() {
        return itemRarity;
    }
}
