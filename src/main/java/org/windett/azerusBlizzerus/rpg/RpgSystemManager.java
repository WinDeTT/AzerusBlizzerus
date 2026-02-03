package org.windett.azerusBlizzerus.rpg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.events.listeners.rpg.entity.RpgEntityDeathListener;
import org.windett.azerusBlizzerus.events.listeners.rpg.entity.mob.RpgMobListener;
import org.windett.azerusBlizzerus.rpg.entity.RpgEntityManager;
import org.windett.azerusBlizzerus.rpg.item.RpgItemManager;

public class RpgSystemManager {

    public RpgItemManager getRpgItemManager() {
        return rpgItemManager;
    }

    public RpgEntityManager getRpgEntityManager() {
        return rpgEntityManager;
    }

    private final RpgItemManager rpgItemManager;
    private final RpgEntityManager rpgEntityManager;

    public RpgSystemManager() {
        this.rpgItemManager = new RpgItemManager();
        this.rpgEntityManager = new RpgEntityManager();

        PluginManager pm = Bukkit.getPluginManager();
        final RpgMobListener rpgMobListener = new RpgMobListener();
        final RpgEntityDeathListener rpgEntityDeathListener = new RpgEntityDeathListener();
        pm.registerEvents(rpgMobListener, Main.instance);
        pm.registerEvents(rpgEntityDeathListener, Main.instance);
    }
}
