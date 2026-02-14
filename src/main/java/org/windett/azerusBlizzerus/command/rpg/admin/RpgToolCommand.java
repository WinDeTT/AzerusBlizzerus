package org.windett.azerusBlizzerus.command.rpg.admin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.rpg.item.RpgItem;
import org.windett.azerusBlizzerus.rpg.item.RpgItemManager;
import org.windett.azerusBlizzerus.rpg.item.RpgItemStack;

import java.util.List;

public class RpgToolCommand extends BukkitCommand {

    public RpgToolCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().info("Команда не может быть выполнена из консоли!");
            return true;
        }
        if (args.length < 1) {
            for (String line : usageTip) {
                player.sendMessage(Component.text(line));
            }
            return true;
        }
        switch (args[0]) {
            case "item" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: /rpg item get <id> <amount>"));
                    return true;
                }
                switch (args[1]) {
                    case "get" -> {
                        int id;
                        int amount;

                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(Component.text("Пожалуйста, укажите ID предмета:"));
                            player.sendMessage(Component.text("/rpg item get <id> [<amount>]"));
                            return true;
                        }
                        if (id < 0) {
                            player.sendMessage(Component.text("Предмет ме может иметь отрицательный номер идентификатора!"));
                            return true;
                        }
                        ItemStack rpgItem = RpgItemStack.rpgItemStack(id);
                        if (rpgItem == null) {
                            player.sendMessage(Component.text("Предмета с указанным идентификатором не существует!"));
                            return true;
                        }
                        rpgItem.setAmount(1);

                        if (args.length > 3) {
                            try {
                                amount = Integer.parseInt(args[3]);
                                rpgItem.setAmount(amount);
                            } catch (NumberFormatException ex) {
                                player.sendMessage(Component.text("Ошибка при указании количества предметов!"));
                            }
                        }
                        player.getInventory().addItem(rpgItem);
                        RpgItem ri = Main.rpgSystemManager.getRpgItemManager().getRpgItem(id);
                        player.sendMessage(Component.text("Предмет " + ri.getDisplayName()).content() + " успешно выдан вам в инвентарь!");
                    }
                    case "enchant" -> {

                    }
                }
            }
            case "spawn" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, укажите ID сущности:"));
                    player.sendMessage(Component.text("/rpg spawn <id>"));
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    WorldContext playerCtx = Main.tweakManager.getContextManager().getEntityContext(player);

                    Main.rpgSystemManager.getRpgEntityManager().spawnRpgEntity(playerCtx.getContextName(), id, player.getLocation(), null);
                } catch (NumberFormatException ex) {
                    player.sendMessage("Необходимо указать число!");
                    return true;
                }
            }
        }
        return true;
    }



    public List<String> usageTip = List.of(
            "==Использование==",
            "/rpg item get <id> <amount> - выдать себе РПГ-предмет",
            "/rpg item upgrade <level> - улучшить предмет до конкретного уровня",
            "/rpg spawn <id> - создать РПГ-моба"
    );
}
