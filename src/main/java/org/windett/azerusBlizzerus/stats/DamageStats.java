package org.windett.azerusBlizzerus.stats;

public class DamageStats {

    private double physicalDamage = 1.0;
    private double magicalDamage = 1.0;
    private double critChance = 0.0;

    public DamageStats() {}

    public double getPhysicalDamage() {
        return this.physicalDamage;
    }
    public double getMagicalDamage() {
        return this.magicalDamage;
    }
    public double getCritChance() {
        return this.critChance;
    }

    public void setPhysicalDamage(double damage) {
        this.physicalDamage = damage;
    }
    public void setMagicalDamage(double damage) {
        this.magicalDamage = damage;
    }
    public void setCritChance(double chance) {
        this.critChance = chance;
    }
}
