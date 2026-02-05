package org.windett.azerusBlizzerus.events.listeners.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgDamageable;

public class EntityGlobalAttackListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTryAttack(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        final Entity attacker = event.getDamager();
        final Entity damageTaker = event.getEntity();
        final RpgDamageable rpgAttacker = (RpgDamageable) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(attacker);
        if (rpgAttacker == null) {
            return;
        }
        final RpgDamageable rpgDamageTaker = (RpgDamageable) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(damageTaker);
        if (rpgDamageTaker == null) {
            return;
        }
        if (System.currentTimeMillis() - rpgAttacker.getLastAttackMillis() < rpgAttacker.getAttackCooldown()) return;
        rpgDamageTaker.handleDamage(rpgAttacker, event.getFinalDamage());
    }
}
