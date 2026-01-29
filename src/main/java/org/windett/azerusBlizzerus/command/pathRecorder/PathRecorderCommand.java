package org.windett.azerusBlizzerus.command.pathRecorder;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptMoveManager;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptedMovement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PathRecorderCommand extends BukkitCommand {

    public PathRecorderCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof final Player player)) {
            if (sender instanceof ConsoleCommandSender)
                Bukkit.getConsoleSender().sendMessage(Component.text("Команда не может быть выполнена из консоли!"));
            return true;
        }
        if (args.length < 1) {
            for (String line : usageList) {
                player.sendMessage(Component.text(line));
            }
            return true;
        }

        switch (args[0]) {
            case "create" -> {
                ScriptedMovement scriptMove = new ScriptedMovement();
                ScriptMoveManager.mobPaths.put(player.getUniqueId(), scriptMove);
                player.sendMessage(Component.text("Создан новый (чистый) путь!"));
            }
            case "record" -> {
                ScriptedMovement move = ScriptMoveManager.getScriptedMovementFromEntity(player);
                if (move == null) {
                    player.sendMessage(Component.text("Вам необходимо создать новый путь"));
                    player.sendMessage(Component.text("/pathrec create"));
                    return true;
                }
                if (move.getRunnable() != null && !move.getRunnable().isCancelled()) {
                    player.sendMessage(Component.text("В данный момент уже выполняется запись/воспроизведение"));
                    return true;
                }
                move.runStartRecord(player);
            }
            case "play" -> {
                ScriptedMovement move = ScriptMoveManager.getScriptedMovementFromEntity(player);
                if (move == null) {
                    return true;
                }
                if (move.getRunnable() != null && !move.getRunnable().isCancelled()) {
                    return true;
                }
                boolean useOriginalPosition;
                if (args.length > 1) {
                    switch (args[1]) {
                        case "false" -> useOriginalPosition = false;
                        default -> useOriginalPosition = true;
                    }
                } else useOriginalPosition = true;
                move.runReplayRecording(player, 0, useOriginalPosition, null);
                player.sendMessage(Component.text("Начато воспроизведение записанного ранее пути."));
            }
            case "stop" -> {
                ScriptedMovement move = ScriptMoveManager.getScriptedMovementFromEntity(player);
                if (move == null) {
                    return true;
                }
                if (move.getRunnable() == null || move.getRunnable().isCancelled()) {
                    return true;
                }
                move.stop(player, false);
                player.sendMessage(Component.text("Текущая операция (запись/воспроизведение) остановлена!"));
            }
            case "launch" -> {
                ScriptedMovement movement = ScriptMoveManager.getScriptedMovementFromEntity(player);
                if (movement == null) {
                    return true;
                }
                if (movement.getRunnable() != null && !movement.getRunnable().isCancelled()) {
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Пожалуйста, укажите тип сущности:"));
                    player.sendMessage(Component.text("/pathrec launch <entityType>"));
                    return true;
                }
                WorldContext playerContext = ContextManager.getEntityContext(player);
                if (playerContext == null) {
                    player.sendMessage(Component.text("Ошибка! У вас не установлен контекст!"));
                    return true;
                }

                Entity launched;
                EntityType type;
                try {
                    type = EntityType.valueOf(args[1].toUpperCase(Locale.ROOT));
                    launched = playerContext.spawnEntity(player.getLocation(), type);
                } catch (IllegalArgumentException ex) {
                    // player.sendMessage(Component.text("Ошибка! Указан неверный тип сущности!"));
                    Bukkit.getLogger().info(ex.getMessage());
                    return true;
                }
                if (launched == null) {
                    return true;
                }

                boolean useStartLocation = true;
                if (args.length > 2) {
                    useStartLocation = Boolean.parseBoolean(args[2]);
                }

                movement.runReplayRecording(launched, 0, useStartLocation, launched::remove);
            }
            default -> {
                return true;
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        if (ScriptMoveManager.mobPaths.containsKey(player.getUniqueId())) {
            if (args.length == 1) {
                return List.of("create", "record", "play", "stop", "launch");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("play")) {
                    return List.of("true", "false");
                }
                else if (args[0].equalsIgnoreCase("launch")) {
                    List<String> entities = new ArrayList<>();
                    for (EntityType type : EntityType.values()) {
                        entities.add(type.name().toLowerCase());
                    }
                    return entities;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("launch")) {
                    return List.of("true", "false");
                }
            }
        }
        else {
            if (args.length == 1) {
                return List.of("create");
            }
        }

        return List.of();
    }

    public List<String> usageList = List.of(
            "==РАБОТА СО СКРИПТОВЫМ МАРШРУТОМ==",
            "/pathrec create - создать чистый (новый) путь",
            "/pathrec record - начать запись пути",
            "/pathrec play <true/false> - начать проигрывание пути)",
            "(true - будет использована оригинальная стартовая позиция. false - текущая.",
            "/pathrec stop - остановить проигрывание пути",
            "/pathrec launch <entityType> <true/false> - запустить сущность на записанном пути",
            "(true - будет использована оригинальная стартовая позиция. false - текущая."
    );
}
