package org.windett.azerusBlizzerus.rpg.player.data;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private int level;
    private double xp;
    private double gold;
    private PlayerStorageData playerStorageData;
    private PlayerQuestData playerQuestData;
    private boolean inCutscene = false;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.level = 1;
        this.xp = 0.0;
        this.gold = 0.0;
        this.playerStorageData = new PlayerStorageData();
        this.playerQuestData = new PlayerQuestData();
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public int getLevel() {
        return this.level;
    }

    public double getXp() {
        return this.xp;
    }

    public double getGold() {
        return this.gold;
    }

    public PlayerStorageData getStorageData() {
        return this.playerStorageData;
    }

    public boolean isInCutscene() {
        return this.inCutscene;
    }
    public void setInCutscene(boolean inCutscene) {
        this.inCutscene = inCutscene;
    }

}
