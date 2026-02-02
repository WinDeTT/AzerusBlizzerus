package org.windett.azerusBlizzerus.rpg.entity;

import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

public interface RpgDamageable {

    int getLevel();
    double getHealth();
    double getMaxHealth();
    void setHealth(int health);

    DamageStats getDamageStats();
    DefenceStats getDefenceStats();

    void onDamage();
    void onDeath();

}
