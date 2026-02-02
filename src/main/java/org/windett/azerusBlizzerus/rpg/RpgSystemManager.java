package org.windett.azerusBlizzerus.rpg;

import org.windett.azerusBlizzerus.content.contentBase.RpgMobBase;
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

    }
}
