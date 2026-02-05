package org.windett.azerusBlizzerus.rpg.entity;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.windett.azerusBlizzerus.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DamagersTracker {

    private static final long LONG_INACTIVE_TIME = 10000L;
    private final Map<RpgPlayer, Pair<Double, Long>> damageMap = new HashMap<>();
    private BukkitTask trackerRunnable = null;

    public DamagersTracker(RpgMob rpgMob, RpgPlayer rpgPlayer, double damage) {
        merge(rpgPlayer, damage);

        trackerRunnable = new BukkitRunnable() {
            public void run() {
                Set<RpgPlayer> damagers = damageMap.keySet();
                long currentMillis = System.currentTimeMillis();
                for (RpgPlayer damager : damagers) {
                    if (currentMillis - damageMap.get(damager).second() >= LONG_INACTIVE_TIME) {
                        damageMap.remove(damager);
                    }
                }
                if (damagers.isEmpty()) {
                    stop();
                    rpgMob.damagersTracker = null;
                    rpgMob.restoreHealth();
                }
            }
        }.runTaskTimer(Main.instance, 20L, 20L);
    }

    public void merge(RpgPlayer rpgPlayer, double damage) {
        if (damage <= 0) return;
        Pair<Double, Long> trackedDamage = damageMap.get(rpgPlayer);
        if (trackedDamage == null) {
            this.damageMap.put(rpgPlayer, Pair.of(damage, System.currentTimeMillis()));
            return;
        }
        double updatedDamage = trackedDamage.first() + damage;
        this.damageMap.put(rpgPlayer, Pair.of(updatedDamage, System.currentTimeMillis()));
    }

    public void stop() {
        if (trackerRunnable != null && !trackerRunnable.isCancelled()) {
            trackerRunnable.cancel();
        }
        trackerRunnable = null;
        damageMap.clear();
    }

    public Map<RpgPlayer, Double> getDamageMap() {
        Map<RpgPlayer, Double> playerDamageMap = new HashMap<>();
        Map<RpgPlayer, Pair<Double, Long>> defaultMap = Map.copyOf(damageMap);
        for (RpgPlayer rpgPlayer : defaultMap.keySet()) {
            if (rpgPlayer == null) continue;;
            if (!rpgPlayer.isValid()) continue;
            playerDamageMap.put(rpgPlayer, defaultMap.get(rpgPlayer).first());
        }
        return playerDamageMap;
    }
}
