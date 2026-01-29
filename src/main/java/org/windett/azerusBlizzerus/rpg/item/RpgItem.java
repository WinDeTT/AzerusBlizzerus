package org.windett.azerusBlizzerus.rpg.item;

import org.bukkit.Material;
import org.windett.azerusBlizzerus.rpg.item.enums.WeaponEnum;

public class RpgItem {

    private final int id;
    private Material material;
    private ItemType itemType;
    private ItemRarity itemRarity;
    private WeaponEnum.WeaponType weaponType;
    private WeaponEnum.WeaponSubType weaponSubType;

    public RpgItem(Builder builder) {
        this.id = builder.id;
        this.material = builder.material;
        this.itemType = builder.itemType;
        this.itemRarity = builder.itemRarity;
        this.weaponType = builder.weaponType;
        this.weaponSubType = builder.weaponSubType;
    }

    public static class Builder {
        private int id = 0;
        private Material material = Material.STONE;
        private ItemType itemType = ItemType.RESOURCE;
        private ItemRarity itemRarity = ItemRarity.COMMON;
        private WeaponEnum.WeaponType weaponType = WeaponEnum.WeaponType.MELEE;
        private WeaponEnum.WeaponSubType weaponSubType = WeaponEnum.WeaponSubType.SWORD;

        public Builder id(int id) {
            this.id = id;
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
        COMMON("&f", "Обычное", 0),
        UNCOMMON("&2", "Необычное", 1),
        RARE("&9", "Редкое", 2),
        EPIC("&d", "Эпическое", 3),
        LEGENDARY("&6", "Легендарное", 4),
        MYTHIC("&5", "Мифическое", 5),
        ARTEFACT("&c", "Артефакт", 6);

        public String getColorCode() {
            return colorCode;
        }
        public String getDisplayName() {
            return displayName;
        }
        public int getPriority() {
            return priority;
        }

        private final String colorCode;
        private final String displayName;
        private final int priority;

        ItemRarity(String colorCode, String displayName, int priority) {
            this.colorCode = colorCode;
            this.displayName = displayName;
            this.priority = priority;
        }
    }
}
