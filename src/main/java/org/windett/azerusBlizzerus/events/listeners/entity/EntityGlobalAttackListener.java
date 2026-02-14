package org.windett.azerusBlizzerus.events.listeners.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
        final LivingEntity attacker = (LivingEntity) event.getDamager();
        final LivingEntity damageTaker = (LivingEntity) event.getEntity();
        final RpgDamageable rpgAttacker = (RpgDamageable) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(attacker);
        if (rpgAttacker == null) {
            return;
        }
        final RpgDamageable rpgDamageTaker = (RpgDamageable) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(damageTaker);
        if (rpgDamageTaker == null) {
            return;
        }
        if (damageTaker instanceof Player) {
            return;
        }
        if (System.currentTimeMillis() - rpgAttacker.getLastAttackMillis() < rpgAttacker.getAttackDelay()) return;
        double damage = event.getFinalDamage();
        rpgDamageTaker.handleDamage(rpgAttacker, damage);
    }
}
