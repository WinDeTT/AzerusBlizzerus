package org.windett.azerusBlizzerus.rpg.player.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class PlayerQuestData {

    private final Map<Integer, Map<String, Integer>> questMap;

    public PlayerQuestData() {
        this.questMap = new TreeMap<>();
        initFirst();
    }

    public void initFirst() {
        this.questMap.put(1, createQuestCondition("", 0));
    }

    public Map<String, Integer> createQuestCondition(String textedPhase, int defaultCounterValue) {
        return Map.of(textedPhase, defaultCounterValue);
    }

    public Map<String, Integer> getCurrentQuestConditionMap (int questId) {
        return this.questMap.getOrDefault(questId, null);
    }
}
