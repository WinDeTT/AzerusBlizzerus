package org.windett.azerusBlizzerus.events.listeners.rpg.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgDamageable;

public class RpgEntityDeathListener implements Listener {

    @EventHandler
    public void death(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        RpgDamageable rpgEntity = (RpgDamageable) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(entity);
        if (rpgEntity == null) return;
        rpgEntity.handleDeath();
    }
}
