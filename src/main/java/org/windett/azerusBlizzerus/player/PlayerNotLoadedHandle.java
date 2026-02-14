package org.windett.azerusBlizzerus.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.windett.azerusBlizzerus.rpg.player.data.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNotLoadedHandle {

    private final Map<UUID, Inventory> notLoadedDataInventoryCache = new HashMap<>();
    private final Map<UUID, Map<Integer, PlayerCharacter>> playerCharacterSelectionCache = new HashMap<>();



    public void createCharacterSelectionGUI(Player player) {

    }

}
