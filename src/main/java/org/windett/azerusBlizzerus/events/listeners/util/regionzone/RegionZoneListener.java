package org.windett.azerusBlizzerus.events.listeners.util.regionzone;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.events.custom.regionzone.RegionZoneJoinEvent;
import org.windett.azerusBlizzerus.events.custom.regionzone.RegionZoneLeaveEvent;
import org.windett.azerusBlizzerus.persistenceData.NamespacedHelper;

import java.util.Objects;

public class RegionZoneListener implements Listener {


    public boolean isRegionZoneTool(ItemStack stack) {
        if (stack == null) return false;
        final ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(NamespacedHelper.isRegionToolAxeKey);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!isRegionZoneTool(player.getInventory().getItemInMainHand())) return;
        event.setCancelled(true);
        String formattedMessage;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Location selBLoc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
            Main.tweakManager.getRegionZoneManager().select(player, null, selBLoc);
            formattedMessage = String.format("Выделена вторая точка: %.1f, %.1f, %.1f, ", selBLoc.getX(), selBLoc.getY(), selBLoc.getZ());
            player.sendMessage(Component.text(formattedMessage));
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Location selALoc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
            Main.tweakManager.getRegionZoneManager().select(player, selALoc, null);
            formattedMessage = String.format("Выделена первая точка: %.1f, %.1f, %.1f, ", selALoc.getX(), selALoc.getY(), selALoc.getZ());
            player.sendMessage(Component.text(formattedMessage));
        }
    }

    @EventHandler
    public void blockState(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!isRegionZoneTool(player.getInventory().getItemInMainHand())) return;
        event.setCancelled(true);
        final Location selALoc = Objects.requireNonNull(event.getBlock()).getLocation();
        Main.tweakManager.getRegionZoneManager().select(player, selALoc, null);
        String formattedMessage = String.format("Выделена первая точка: %.1f, %.1f, %.1f, ", selALoc.getX(), selALoc.getY(), selALoc.getZ());
        player.sendMessage(Component.text(formattedMessage));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        Main.tweakManager.getRegionZoneManager().checkAndUpdateEntityRegionStatus(player);
    }

    @EventHandler
    public void joinZone(RegionZoneJoinEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Main.tweakManager.getRegionZoneManager().getDebugViewers().contains(player)) return;
        player.sendMessage(Component.text("Вы зашли в регион: " + event.getRegionZone().getRegionName()));
    }

    @EventHandler
    public void leaveZone(RegionZoneLeaveEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Main.tweakManager.getRegionZoneManager().getDebugViewers().contains(player)) return;
        player.sendMessage(Component.text("Вы вышли из региона: " + event.getRegionZone().getRegionName()));
    }
}
