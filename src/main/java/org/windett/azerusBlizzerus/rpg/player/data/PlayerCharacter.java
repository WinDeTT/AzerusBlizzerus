package org.windett.azerusBlizzerus.rpg.player.data;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgPlayer;
import org.windett.azerusBlizzerus.rpg.level.RpgLeveling;
import org.windett.azerusBlizzerus.rpg.message.RpgGlobalMessage;
import org.windett.azerusBlizzerus.stats.DamageStats;
import org.windett.azerusBlizzerus.stats.DefenceStats;

public class PlayerCharacter {

    private final RpgPlayer rpgPlayer;
    private int level;
    private double xp;
    private double gold;
    private final DamageStats damageStats;
    private final DefenceStats defenceStats;

    private PlayerQuestData playerQuestData;

    public PlayerCharacter(RpgPlayer rpgPlayer) {
        this.rpgPlayer = rpgPlayer;
        this.level = 1;
        this.xp = 0.0;
        this.gold = 0.0;
        this.damageStats = new DamageStats();
        this.defenceStats = new DefenceStats();
        this.playerQuestData = new PlayerQuestData();
        this.playerQuestData.initFirst();
    }


    public RpgPlayer getRpgPlayer() {
        return rpgPlayer;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.xp = 0.0;
    }

    public double getXp() {
        return xp;
    }
    public double getRequirementXp(int level) {
        RpgLeveling rpgLeveling = Main.rpgSystemManager.getRpgLeveling();
        if (!rpgLeveling.getRpgLevelingMap().containsKey(level)) return -1;
        return rpgLeveling.getRpgLevelingMap().get(level);
    }

    public boolean isMaxLevel(int level) {
        RpgLeveling rpgLeveling = Main.rpgSystemManager.getRpgLeveling();
        return rpgLeveling.getRpgLevelingMap().get(level) == -1.0;
    }

    public void addXp(double addedXp) {
        RpgLeveling rpgLeveling = Main.rpgSystemManager.getRpgLeveling();
        if (isMaxLevel(this.level)) return;
        if (addedXp <= 0.0) return;

        Component xpMessage = RpgGlobalMessage.INFO.append(Component.text("+" + addedXp).color(RpgGlobalMessage.VALUE_COLOR).append(Component.text(" опыта").color(RpgGlobalMessage.INFO_TEXT_COLOR)));
        rpgPlayer.asBukkitEntity().sendMessage(xpMessage);

        int newLevel = this.level;
        double newXp = this.xp;
        double remainingXp = addedXp;

        while (remainingXp > 0.0) {
            // ВАЖНО: Используем newLevel, а не this.level!
            double neededForNextLevel = getRequirementXp(newLevel) - newXp;

            // Проверяем не максимальный ли уровень
            if (neededForNextLevel <= 0) {
                break; // Максимальный уровень достигнут
            }

            if (remainingXp >= neededForNextLevel) {
                // Переход на следующий уровень
                remainingXp -= neededForNextLevel;
                newLevel++;
                newXp = 0;

                // Показываем титул
                Player player = (Player) rpgPlayer.asBukkitEntity();
                player.sendTitle("Уровень повышен!",
                        "Вы достигли " + newLevel + " уровня",
                        5, 40, 20);

            } else {
                // Недостаточно для перехода
                newXp += remainingXp;
                remainingXp = 0;
            }
        }

        this.level = newLevel;
        this.xp = newXp;
        rpgPlayer.updateLevelScale();
    }

    public void takeXp(double xp) {
        this.xp-=xp;
        if (this.xp < 0) this.xp = 0;
    }

    public double getGold() {
        return gold;
    }
    public void setGold(double gold) {
        this.gold = gold;
    }
    public void addGold(double gold) {
        this.gold+=gold;
    }

    public DamageStats getDamageStats() {
        return damageStats;
    }
    public DefenceStats getDefenceStats() {
        return defenceStats;
    }
}
