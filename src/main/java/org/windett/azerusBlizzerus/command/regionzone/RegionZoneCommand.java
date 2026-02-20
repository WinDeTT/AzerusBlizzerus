package org.windett.azerusBlizzerus.command.regionzone;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.persistenceData.NamespacedHelper;
import org.windett.azerusBlizzerus.utils.regionzone.RegionZone;

import java.util.List;

public class RegionZoneCommand extends BukkitCommand {
    public RegionZoneCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
        setPermission("plugin.regionzone");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().info("Команда не может быть выполнена из консоли!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Component.text("tool / check / create / remove"));
            return true;
        }
        switch (args[0]) {
            case "tool" -> {
                ItemStack toolStack = ItemStack.of(Material.WOODEN_AXE);
                ItemMeta toolMeta = toolStack.getItemMeta();
                toolMeta.setDisplayName("Топорик для выделения");
                toolMeta.getPersistentDataContainer().set(NamespacedHelper.isRegionToolAxeKey, PersistentDataType.BOOLEAN, true);
                toolStack.setItemMeta(toolMeta);
                player.getInventory().addItem(toolStack);
                player.sendMessage(Component.text("Вам выдан инструмент для выделения точек региона."));
            }
            case "check" -> {
                List<RegionZone> regions = Main.tweakManager.getRegionZoneManager().getEntityCurrentRegions(player);
                if (regions.isEmpty()) {
                    player.sendMessage(Component.text("Вы не находитесь в регионе!"));
                } else {
                    player.sendMessage(Component.text("Регионы:"));
                    for (RegionZone zone : regions) {
                        player.sendMessage(Component.text("-" + zone.getRegionName()));
                    }
                }
            }
            case "create" -> {

                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: /regionzone create <name>"));
                    return true;
                }
                Pair<Location, Location> selection = Main.tweakManager.getRegionZoneManager().getSelection(player);
                if (selection == null) {
                    player.sendMessage(Component.text("Вы не выделили территорию!"));
                    return true;
                }
                if (selection.first() == null) {
                    player.sendMessage(Component.text("Вы не выделили первую точку!"));
                    return true;
                }
                if (selection.second() == null) {
                    player.sendMessage(Component.text("Вы не выделили первую точку!"));
                    return true;
                }
                Main.tweakManager.getRegionZoneManager().registerRegion(player.getWorld(), new RegionZone(args[1], player.getWorld(), selection.first(), selection.second()));
                player.sendMessage(Component.text("Регион " + args[1] + " успешно создан!"));
            }

            case "pos1" -> {
                Location playerLocation = player.getLocation();
                Main.tweakManager.getRegionZoneManager().select(player, playerLocation, null);
                String formattedMessage = String.format("Выделена первая точка: %.1f, %.1f, %.1f, ", playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
                player.sendMessage(Component.text(formattedMessage));
            }
            case "pos2" -> {
                Location playerLocation = player.getLocation();
                Main.tweakManager.getRegionZoneManager().select(player, null, playerLocation);
                String formattedMessage = String.format("Выделена вторая точка: %.1f, %.1f, %.1f, ", playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
                player.sendMessage(Component.text(formattedMessage));
            }
            case "debug" -> {
                List<Player> debugViewers = Main.tweakManager.getRegionZoneManager().getDebugViewers();
                if (!debugViewers.contains(player)) {
                    Main.tweakManager.getRegionZoneManager().addDebugViewer(player);
                    player.sendMessage(Component.text("Включена отладка для регионов!"));
                }
                else {
                    Main.tweakManager.getRegionZoneManager().removeDebugViewer(player);
                    player.sendMessage(Component.text("Отключена отладка для регионов!"));
                }
            }
        }
        return false;
    }
}
