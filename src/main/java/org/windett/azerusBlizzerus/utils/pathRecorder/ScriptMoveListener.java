package org.windett.azerusBlizzerus.utils.pathRecorder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.windett.azerusBlizzerus.Main;

public class ScriptMoveListener implements Listener {


    @EventHandler
    public void checkClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        ScriptedMovement movement = Main.tweakManager.getScriptMoveManager().getScriptedMovementFromEntity(player);
        if (movement == null) return;
        if (movement.getCurrentIndex() < 1) return;
        if (movement.getRunnable() == null || movement.getRunnable().isCancelled()) return;
        if (movement.getPathMode() == ScriptedMovement.Mode.REPLAYING) return;
        event.setCancelled(true);
        int currentIndex = movement.getCurrentIndex() - 1;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                movement.getPathList().get(currentIndex).setWasClicking(false, true);
                player.sendMessage("Совершен правый клик");
            }
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                movement.getPathList().get(currentIndex).setWasClicking(true, false);
                player.sendMessage("Совершен левый клик");
            }
        }
    }
}
