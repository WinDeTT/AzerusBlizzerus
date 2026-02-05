package org.windett.azerusBlizzerus.rpg.entity;

import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

import java.util.List;

public interface RpgDamageable {

    int getLevel();
    double getHealth();
    double getMaxHealth();
    void restoreHealth();
    void setHealth(double health);

    DamageStats getDamageStats();
    DefenceStats getDefenceStats();

    long getAttackCooldown();
    long getLastAttackMillis();
    void updateAttackCooldown();
    void handleDamage(double damage);
    void handleDamage(RpgDamageable attacker, double damage);
    void handleDeath();

    List<RpgPlayer> getNearbyPlayers();

}
