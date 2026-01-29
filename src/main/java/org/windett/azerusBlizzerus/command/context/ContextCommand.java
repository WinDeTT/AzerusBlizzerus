package org.windett.azerusBlizzerus.command.context;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.context.WorldContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ContextCommand extends BukkitCommand {

    public ContextCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
        setPermission("plugin.context");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof final Player player)) {
            if (sender instanceof ConsoleCommandSender) {
                ConsoleCommandSender console = Bukkit.getConsoleSender();
                console.sendMessage(Component.text("Команда не может быть вызвана из консоли!"));
            }
            return true;
        }
        if (args.length < 1) {
            showUsage(player);
            return true;
        }
        switch (args[0]) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, укажите название!"));
                    player.sendMessage(Component.text("/context create <contextName>"));
                    return true;
                }
                try {
                    ContextManager.registerContext(args[1], false);
                    player.sendMessage("Контекст " + args[1] + " был успешно зарегистрирован!");
                } catch (Exception ex) {
                    player.sendMessage(ex.getMessage());
                }
            }
            case "enter" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, укажите название!"));
                    player.sendMessage(Component.text("/context enter <contextName>"));
                    return true;
                }
                try {
                    ContextManager.moveToContext(player, args[1]);
                    player.sendMessage("Вы были перемещены в контекст " + args[1]);
                }
                catch (Exception ex) {
                    player.sendMessage(ex.getMessage());
                }
            }
            case "list" -> {
                if (ContextManager.contextMap.isEmpty()) {
                    player.sendMessage("Ни одного контекста не создано!");
                }
                player.sendMessage("Список доступных контекстов:");
                for (String name : ContextManager.contextMap.keySet()) {
                    player.sendMessage("- " + name);
                }
            }
            case "my" -> {
                String contextName = ContextManager.getEntityContextName(player);
                player.sendMessage("Ваш текущий контекст: " + contextName);
            }
            case "spawn" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, введите тип сущности:"));
                    player.sendMessage(Component.text("/context spawn <entityType>"));
                    return true;
                }
                EntityType type = EntityType.ZOMBIE;
                try {
                    type = EntityType.valueOf(args[1].toUpperCase(Locale.ROOT));
                }
                catch (NullPointerException ex) {
                    player.sendMessage("Указан неверный тип сущности! Был создан ZOMBIE");
                }
                Entity entity = player.getWorld().spawnEntity(player.getLocation(), type);
                String playerContextName = ContextManager.getEntityContextName(player);
                ContextManager.moveToContext(entity, playerContextName);
                entity.customName(Component.text("Контекст: " + ContextManager.getEntityContextName(entity)));
                entity.setCustomNameVisible(true);
            }
            case "update" -> {
                ContextManager.refreshPlayerContextVisibility(player);
                player.sendMessage("Отображение вашего контекста обновлено!");
            }
            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, введите название контекста:"));
                    player.sendMessage(Component.text("/context remove <contextName>"));
                    return true;
                }
                if (!ContextManager.contextMap.containsKey(args[1])) {
                    player.sendMessage(Component.text("Указанного контекста не существует!"));
                    return true;
                }
                try {
                    ContextManager.removeContext(args[1]);
                    player.sendMessage(Component.text("Контекст " + args[1] + " был успешно удалён!"));
                } catch (IllegalPluginAccessException ex) {
                    player.sendMessage(Component.text(ex.getMessage()));
                }
            }
            default -> showUsage(player);
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender,
                                             @NotNull String alias,
                                             @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "enter", "list", "my", "spawn", "update", "remove");
        }
        else if (args.length == 2) {
            final String firstArg = args[0].toLowerCase();
            switch(args[0]) {
                case "enter" -> {
                    return new ArrayList<>(ContextManager.contextMap.keySet());
                }
                case "spawn" -> {
                    List<String> entityTypes = new ArrayList<>();
                    for (EntityType type : EntityType.values()) {
                        entityTypes.add(type.name().toLowerCase());
                    }
                    return entityTypes;
                }
                case "remove" -> {
                    List<String> contextNameList = new ArrayList<>();
                    for (String contextName : ContextManager.contextMap.keySet()) {
                        WorldContext context = ContextManager.contextMap.get(contextName);
                        if (context.isLockedToRemove()) continue;
                        contextNameList.add(contextName);
                    }
                    return contextNameList;
                }
            }
        }
        return List.of();
    }


    public void showUsage(Player player) {
        player.sendMessage(Component.text("/context create <name> - зарегистрировать новый контекст"));
        player.sendMessage(Component.text("/context enter <name> - перейти в другой контекст"));
        player.sendMessage(Component.text("/context list - посмотреть список доступных контекстов"));
        player.sendMessage(Component.text("/context my - узнать название текущего контекста"));
        player.sendMessage(Component.text("/context update - обновить для себя видимость сущностей текущего контекста"));
        player.sendMessage(Component.text("/context spawn <entityType> - создать сущность в текущем контексте"));
    }
}
