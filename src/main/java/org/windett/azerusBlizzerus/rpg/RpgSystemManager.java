package org.windett.azerusBlizzerus.rpg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.events.listeners.rpg.entity.RpgEntityDeathListener;
import org.windett.azerusBlizzerus.events.listeners.rpg.entity.mob.RpgMobListener;
import org.windett.azerusBlizzerus.rpg.entity.RpgEntityManager;
import org.windett.azerusBlizzerus.rpg.item.RpgItemManager;
import org.windett.azerusBlizzerus.rpg.level.RpgLeveling;

public class RpgSystemManager {

    public RpgItemManager getRpgItemManager() {
        return rpgItemManager;
    }

    public RpgEntityManager getRpgEntityManager() {
        return rpgEntityManager;
    }

    public RpgLeveling getRpgLeveling() {
        return rpgLeveling;
    }

    private final RpgLeveling rpgLeveling;
    private final RpgItemManager rpgItemManager;
    private final RpgEntityManager rpgEntityManager;

    public RpgSystemManager() {
        this.rpgLeveling = new RpgLeveling();
        this.rpgItemManager = new RpgItemManager();
        this.rpgEntityManager = new RpgEntityManager();

        PluginManager pm = Bukkit.getPluginManager();
        final RpgMobListener rpgMobListener = new RpgMobListener();
        final RpgEntityDeathListener rpgEntityDeathListener = new RpgEntityDeathListener();
        pm.registerEvents(rpgMobListener, Main.instance);
        pm.registerEvents(rpgEntityDeathListener, Main.instance);
    }
}
