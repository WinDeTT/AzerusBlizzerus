package org.windett.azerusBlizzerus;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.windett.azerusBlizzerus.command.camera.CameraCommand;
import org.windett.azerusBlizzerus.command.context.ContextCommand;
import org.windett.azerusBlizzerus.command.pathRecorder.PathRecorderCommand;
import org.windett.azerusBlizzerus.command.rpg.admin.RpgToolCommand;
import org.windett.azerusBlizzerus.command.rpg.item.RecipeOpenCommand;
import org.windett.azerusBlizzerus.content.contentBase.RpgItemBase;
import org.windett.azerusBlizzerus.content.contentBase.RpgMobBase;
import org.windett.azerusBlizzerus.content.contentBase.RpgSpawnerBase;
import org.windett.azerusBlizzerus.context.ContextListener;
import org.windett.azerusBlizzerus.events.listeners.entity.EntityGlobalAttackListener;
import org.windett.azerusBlizzerus.events.listeners.entity.player.PlayerListenerJoinQuit;
import org.windett.azerusBlizzerus.rpg.RpgSystemManager;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptMoveListener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class Main extends JavaPlugin {

    public static Main instance;
    public static File cameraRecsFolder;
    public static File pathRecsFolder;
    public static TweakManager tweakManager;
    public static RpgSystemManager rpgSystemManager;

    public boolean isServerIsReady() {
        return serverIsReady;
    }

    public boolean serverIsReady = false;

    @Override
    public void onEnable() {
        instance = this;


        Bukkit.clearRecipes();

        try {
            final ServerFile serverFile = new ServerFile();
            cameraRecsFolder = serverFile.getCameraRecsFolder();
            pathRecsFolder = serverFile.getPathRecsFolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final PluginManager pm = Bukkit.getPluginManager();
        tweakManager = new TweakManager();
        rpgSystemManager = new RpgSystemManager();
        rpgSystemManager.getRpgItemManager().registerRecipes();
        final RpgMobBase rpgMobBase = new RpgMobBase();
        rpgMobBase.init();
        final RpgItemBase rpgItemBase = new RpgItemBase();

        final PlayerListenerJoinQuit playerListenerJoinQuit = new PlayerListenerJoinQuit();
        final ScriptMoveListener scriptMoveListener = new ScriptMoveListener();
        final ContextListener ctxListen = new ContextListener();
        final EntityGlobalAttackListener entityGlobalAttackListener = new EntityGlobalAttackListener();


        pm.registerEvents(playerListenerJoinQuit, this);
        pm.registerEvents(scriptMoveListener, this);
        pm.registerEvents(ctxListen, this);
        pm.registerEvents(entityGlobalAttackListener, this);

        CommandMap commandMap = Bukkit.getCommandMap();
        RecipeOpenCommand recipeOpenCommand = new RecipeOpenCommand("openrecipe", "Recipe management", "/openrecipe", List.of("oprec"));
        ContextCommand ctxCommand = new ContextCommand("context", "Context management", "/context", Arrays.asList("ctx", "ct"));
        CameraCommand camCommand = new CameraCommand("camera", "Camera creation", "/camera", List.of("cam"));
        PathRecorderCommand pathRecCommand = new PathRecorderCommand("pathRecorder", "Path Recorder management", "/pathrec", List.of("pr"));
        RpgToolCommand rpgToolCommand = new RpgToolCommand("rpgTool", "Rpg management", "/rpg", List.of("rpg"));
        commandMap.register("azerusblizzerus", recipeOpenCommand);
        commandMap.register("azerusblizzerus", ctxCommand);
        commandMap.register("azerusblizzerus", camCommand);
        commandMap.register("azerusblizzerus", pathRecCommand);
        commandMap.register("azerusblizzerus", rpgToolCommand);

        new BukkitRunnable() {
            public void run() {
                if (Bukkit.getWorld("world") != null) {
                    cancel();
                    RpgSpawnerBase rpgSpawnerBase = new RpgSpawnerBase();
                    tweakManager.getContextManager().registerGlobalContext();
                    serverIsReady = true;
                }
            }
        }.runTaskTimer(Main.instance, 40L, 40L);
    }

    @Override
    public void onDisable() {
        final World world = Bukkit.getWorld("world");
        if (world == null) return;
        final File worldFolder = world.getWorldFolder();
        if (!worldFolder.exists()) return;
        final File worldEntitiesFolder = new File(worldFolder, "entities");
        if (!worldEntitiesFolder.exists() && !worldEntitiesFolder.isDirectory()) return;
        File[] entityFiles = worldEntitiesFolder.listFiles((dir, name) ->
                name.endsWith(".mca") || name.endsWith(".mcc"));

        if (entityFiles == null) return;
        for (File file : entityFiles) {
            if (file.delete()) {
                Bukkit.getLogger().info("Файл" + file.getName() + " успешно удалён!");
            }
            else {
                Bukkit.getLogger().info("Файл" + file.getName() + " удалить не удалось!");
            }
        }
    }
}
