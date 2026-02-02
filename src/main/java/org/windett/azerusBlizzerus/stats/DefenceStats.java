package org.windett.azerusBlizzerus.stats;

public class DefenceStats {

    private double physicalDefence = 0.0;
    private double magicalDefence = 0.0;
    private double dodgeChance = 0.0;

    public DefenceStats() {}

    public double getPhysicalDefence() {
        return this.physicalDefence;
    }
    public double getMagicalDefence() {
        return this.magicalDefence;
    }
    public double getDodgeChance() {
        return this.dodgeChance;
    }

    public void setPhysicalDefence(double defence) {
        this.physicalDefence = defence;
    }
    public void setMagicalDefence(double defence) {
        this.magicalDefence = defence;
    }
    public void setDodgeChance(double chance) {
        this.dodgeChance = chance;
    }
}
