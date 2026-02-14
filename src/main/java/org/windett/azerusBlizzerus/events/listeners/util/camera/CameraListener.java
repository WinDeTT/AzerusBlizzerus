package org.windett.azerusBlizzerus.events.listeners.util.camera;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.windett.azerusBlizzerus.persistenceData.NamespacedHelper;

public class CameraListener implements Listener {


    public boolean isCamera(ItemDisplay entity) {
        if (!entity.isValid()) return false;
        if (!entity.getPersistentDataContainer().has(NamespacedHelper.isCameraKey)) return false;
        return Boolean.TRUE.equals(entity.getPersistentDataContainer().get(NamespacedHelper.isCameraKey, PersistentDataType.BOOLEAN));
    }

    @EventHandler
    public void cameraDismount(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR) return;
        if (player.getSpectatorTarget() == null) return;
        if (!(player.getSpectatorTarget() instanceof ItemDisplay)) return;
        final ItemDisplay cameraEntity = (ItemDisplay) player.getSpectatorTarget();
        if (!isCamera(cameraEntity)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void cameraChangeView(PlayerStopSpectatingEntityEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR) return;
        if (player.getSpectatorTarget() == null) return;
        if (!(player.getSpectatorTarget() instanceof ItemDisplay)) return;
        final ItemDisplay cameraEntity = (ItemDisplay) player.getSpectatorTarget();
        if (!isCamera(cameraEntity)) return;
        event.setCancelled(true);
    }
}
