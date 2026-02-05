package org.windett.azerusBlizzerus.rpg.level;

import java.util.HashMap;
import java.util.Map;

public class RpgLeveling {

    public Map<Integer, Double> getRpgLevelingMap() {
        return rpgLevelingMap;
    }

    private  final Map<Integer, Double> rpgLevelingMap = new HashMap<>();

    public RpgLeveling() {
        rpgLevelingMap.put(1, 1.0);
        rpgLevelingMap.put(2, 2.5);
        rpgLevelingMap.put(3, 4.35);
        rpgLevelingMap.put(4, 7.75);
        rpgLevelingMap.put(5, -1.0);
    }
}
