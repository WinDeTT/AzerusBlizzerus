package org.windett.azerusBlizzerus.content;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

public class ContentRpgEntity {


    private final int id;
    private String name;
    private final EntityType type;
    private final Specific specific;
    private final double maxHealth;
    private final int level;
    private final double xpLoot;
    private final DamageStats damageStats;
    private final DefenceStats defenceStats;
    private final float movementSpeed;
    private final ItemStack hand;
    private final ItemStack offHand;
    private final ItemStack helmet;
    private final ItemStack chestPlate;
    private final ItemStack leggings;
    private final ItemStack boots;


    public enum Specific {
        SIMPLE("&7", "Обычный"),
        ELITE("&c", "Элитный"),
        BOSS("&4", "Босс"),
        RAID("&6", "Рейдовый босс");

        private final String colorCode;
        private final String display;

        Specific(String colorCode, String display) {
            this.colorCode = colorCode;
            this.display = display;
        }
    }

    public ContentRpgEntity(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.specific = builder.specific;
        this.maxHealth = builder.maxHealth;
        this.level = builder.level;
        this.xpLoot = builder.xpLoot;
        this.damageStats = builder.damageStats;
        this.defenceStats = builder.defenceStats;
        this.movementSpeed = builder.movementSpeed;

        this.hand = builder.hand;
        this.offHand = builder.offHand;
        this.helmet = builder.helmet;
        this.chestPlate = builder.chestPlate;
        this.leggings = builder.leggings;
        this.boots = builder.boots;


        Main.rpgSystemManager.getRpgEntityManager().registerContentMob(this.id, this);
    }

    public static class Builder {
        private int id = 1;
        private String name = "content.entity";
        private EntityType type = EntityType.ZOMBIE;
        private Specific specific = Specific.SIMPLE;
        private double maxHealth = 10.0;
        private int level = 1;
        private double xpLoot = 1.0;
        private DamageStats damageStats = new DamageStats();
        private DefenceStats defenceStats = new DefenceStats();
        private float movementSpeed = 0.25F;

        private ItemStack hand = null;
        private ItemStack offHand = null;
        private ItemStack helmet = null;
        private ItemStack chestPlate = null;
        private ItemStack leggings = null;
        private ItemStack boots = null;

        public Builder id(int id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder type(EntityType type) {
            this.type = type;
            return this;
        }
        public Builder specific(Specific specific) {
            this.specific = specific;
            return this;
        }
        public Builder maxHealth(double maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }
        public Builder level(int level) {
            this.level = level;
            return this;
        }
        public Builder xpLoot(double xp) {
            this.xpLoot = xp;
            return this;
        }
        public Builder physicalDamage(double damage) {
            this.damageStats.setPhysicalDamage(damage);
            return this;
        }
        public Builder magicalDamage(double damage) {
            this.damageStats.setMagicalDamage(damage);
            return this;
        }
        public Builder critChance(double chance) {
            this.damageStats.setCritChance(chance);
            return this;
        }
        public Builder physicalDefence(double defence) {
            this.defenceStats.setPhysicalDefence(defence);
            return this;
        }
        public Builder magicalDefence(double defence) {
            this.defenceStats.setMagicalDefence(defence);
            return this;
        }
        public Builder dodgeChance(double chance) {
            this.defenceStats.setDodgeChance(chance);
            return this;
        }
        public Builder speed(float speed) {
            this.movementSpeed = speed;
            return this;
        }


        public Builder hand(Material material) {
            final ItemStack handStack = ItemStack.of(material);
            resumeFlags(handStack);
            this.hand = handStack;
            return this;
        }
        public Builder offHand(Material material) {
            final ItemStack offHandStack = ItemStack.of(material);
            resumeFlags(offHandStack);
            this.offHand = offHandStack;
            return this;
        }
        public Builder helmet(Material material) {
            final ItemStack helmetStack = ItemStack.of(material);
            resumeFlags(helmetStack);
            this.helmet = helmetStack;
            return this;
        }
        public Builder chest(Material material) {
            final ItemStack chestStack = ItemStack.of(material);
            resumeFlags(chestStack);
            this.chestPlate = chestStack;
            return this;
        }
        public Builder leggings(Material material) {
            final ItemStack leggingsStack = ItemStack.of(material);
            resumeFlags(leggingsStack);
            this.leggings = leggingsStack;
            return this;
        }
        public Builder boots(Material material) {
            final ItemStack bootsStack = ItemStack.of(material);
            resumeFlags(bootsStack);
            this.boots = bootsStack;
            return this;
        }

        public ContentRpgEntity build() {
            return new ContentRpgEntity(this);
        }

        public void resumeFlags(ItemStack itemStack) {
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            final ItemMeta meta = itemStack.getItemMeta();
            meta.setUnbreakable(true);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public Specific getSpecific() {
        return specific;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public int getLevel() {
        return level;
    }

    public double getXpLoot() {
        return xpLoot;
    }

    public DamageStats getDamageStats() {
        return damageStats;
    }

    public DefenceStats getDefenceStats() {
        return defenceStats;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public ItemStack getHand() {
        return hand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestPlate() {
        return chestPlate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }
}
