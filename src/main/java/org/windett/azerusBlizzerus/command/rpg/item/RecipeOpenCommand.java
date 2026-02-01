package org.windett.azerusBlizzerus.command.rpg.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.TweakManager;

import java.util.List;

public class RecipeOpenCommand extends BukkitCommand {

    public RecipeOpenCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().info("Команда не может быть выполнена из консоли!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Component.text("Пожалуйста, укажите ID рецепта."));
            player.sendMessage(Component.text("Использование: /recipe <id>"));
            return true;
        }
        final TweakManager manager = Main.tweakManager;
        try {
            int recipeId = Integer.parseInt(args[0]);
            if (!manager.getRpgItemManager().getItemRecipeMap().containsKey(recipeId)) {
                player.sendMessage(Component.text("Рецепта под указанным ID не существует!"));
                return true;
            }
            Main.tweakManager.getRpgItemManager().openRecipe(player, recipeId);
        }
        catch (NumberFormatException ex) {
            player.sendMessage(Component.text("Указан неверный ID рецепта."));
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("1", "2", "...");
        }
        return List.of();
    }
}
