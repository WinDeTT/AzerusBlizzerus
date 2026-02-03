package org.windett.azerusBlizzerus.events.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.utils.cutscene.Scene;
import org.windett.azerusBlizzerus.utils.cutscene.camera.Camera;
import org.windett.azerusBlizzerus.rpg.player.data.PlayerData;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptMoveManager;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptedMovement;

import java.io.FileNotFoundException;
import java.util.*;

public class PlayerJoinQuitListener implements Listener {

    private final ContextManager ctxManager = Main.tweakManager.getContextManager();
    private final ScriptMoveManager scriptMoveManager = Main.tweakManager.getScriptMoveManager();

    final Location worldFirstSpawnLocation = new Location(Bukkit.getWorld("world"), -881.932, 66.0, -1575.988, -89.8F, -0.5F);
    public static final Map<UUID, PlayerData> PLAYERS = new HashMap<>();

    @EventHandler
    public void asyncPreJoin(AsyncPlayerPreLoginEvent event) {
        if (!Main.instance.isServerIsReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Please, wait few seconds..");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        ctxManager.setUpEntityContext(player, "global");
        if (!PLAYERS.containsKey(player.getUniqueId())) {
            PLAYERS.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        }
        e.joinMessage(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(worldFirstSpawnLocation);


        if (player == player) return;


        final String privateStartContext = ("prstart_" + player.getName()).toLowerCase();
        ctxManager.registerContext(privateStartContext, false);

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            Scene joinScene = new Scene(990);
            final Location cam1Loc = new Location(player.getWorld(), -885.8006543867501, 67.59878153753719, -1582.6235599389831, -135.00668F, -5.2507997F);
            final Location cam2Loc = new Location(player.getWorld(), -830.0005797532241, 67.44091360613301, -1575.1540578925183, 91.02105F, -1.9348598F);
            final Location cam3Loc = new Location(player.getWorld(), -857.8025838006636, 124.64981801914698, -1539.3571239701307, -151.81761F, 49.98432F);
            final Location cam4Loc = new Location(player.getWorld(), -797.8324005116565, 91.29266723471953, -1577.6439654487078, 149.38333F, 23.974195F);
            final Location cam5Loc = new Location(player.getWorld(), -896.7468850396976, 88.78331604653515, -1575.0964584746007, -91.77013F, 47.035507F);
            joinScene.createCamera(1, cam1Loc);
            joinScene.createCamera(2, cam2Loc);
            joinScene.createCamera(3, cam3Loc);
            joinScene.createCamera(4, cam4Loc);
            joinScene.createCamera(5, cam5Loc);
            final Map<Integer, Mannequin>[] npcs = new Map[]{null};

            try {
                joinScene.setCameraPath(1, "cut1_c1.yml");
                joinScene.setCameraPath(2, "cut1_c2.yml");
                joinScene.setCameraPath(3, "cut1_c3.yml");
                joinScene.setCameraPath(4, "cut1_c4.yml");
                joinScene.setCameraPath(5, "cut1_c5.yml");
                Bukkit.getLogger().info("Все пути успешно загружены!");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            for (Camera camera : joinScene.cameraList.values()) {
                ctxManager.moveToContext(camera.getCameraEntity(), privateStartContext);
            }
            ctxManager.moveToContext(player, privateStartContext);
            joinScene.setSceneWatcher(player, true);

            joinScene.executeAtStart(() -> {
                joinScene.getCamera(2).setShaking(10, Camera.ShakeIntensive.HIGH);
                joinScene.getCamera(4).setShaking(10, Camera.ShakeIntensive.MEDIUM);
                joinScene.getCamera(5).setShaking(10, Camera.ShakeIntensive.HIGH);
                ctxManager.moveToContext(joinScene.sceneWatcher.getWatcherFakePlayer(), privateStartContext);


                Mannequin npc1 = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -882.924505403004, 67.0, -1545.9963108677773, -179.4089F, 0.0F), EntityType.MANNEQUIN);
                Mannequin npc2 = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -873.7543296874159, 67.0, -1593.589250145356, 90.07669F, 0.0F), EntityType.MANNEQUIN);
                Mannequin npc3 = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -852.1422992631831, 67.0625, -1579.2696771750761, -143.56534F, 0.0F), EntityType.MANNEQUIN);
                Mannequin npc4 = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -892.1604491294094, 67.0, -1547.5753433138395, -90.19462F, 0.0F), EntityType.MANNEQUIN);
                Mannequin npc5 = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -906.7869305710386, 67.0, -1574.1164319097848, -89.40396F, 0.0F), EntityType.MANNEQUIN);
                npcs[0] = Map.of(
                        1, npc1,
                        2, npc2,
                        3, npc3,
                        4, npc4,
                        5, npc5
                );
                for (int number : npcs[0].keySet()) {
                    Mannequin npc = npcs[0].get(number);
                    if (npc == null || !npc.isValid()) continue;
                    ctxManager.moveToContext(npc, privateStartContext);
                    npc.setImmovable(true);
                    npc.setInvulnerable(true);
                    ScriptedMovement movement = new ScriptedMovement();
                    try {
                        scriptMoveManager.loadPathFromFile(player.getWorld(), movement, "cut1_npc" + number + ".yml");
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    movement.runReplayRecording(npc, 0, true, () -> {
                    });
                }


            });

            joinScene.createTickTrigger(1, () -> joinScene.launchCamera(1, 200, 20)); // 30
            joinScene.createTickTrigger(2, () -> joinScene.getCamera(1).setWatcher(player));

            joinScene.createTickTrigger(100, () -> {
                joinScene.sceneWatcher.getWatcher().sendTitle("", "WinDeTT представляет...", 20, 80, 20);
            });
            joinScene.createTickTrigger(160, () -> {
                joinScene.launchCamera(2, 400, 20); // 35
            });
            joinScene.createTickTrigger(170, () -> {
                joinScene.getCamera(2).setWatcher(player);
                joinScene.getCamera(1).setWatcher(null);
                joinScene.removeCamera(1);
            });
            joinScene.createTickTrigger(320, () -> {
                joinScene.launchCamera(3, 400, 20); // 35
            });
            joinScene.createTickTrigger(335, () -> {
                joinScene.getCamera(3).setWatcher(player);
                joinScene.getCamera(2).setWatcher(null);
                joinScene.removeCamera(2);
            });
            joinScene.createTickTrigger(380, () -> {
                joinScene.sceneWatcher.getWatcher().sendTitle("", "АЗЕРУС-БЛИЗЗЕРУС v2.0", 20, 80, 20);
            });
            joinScene.createTickTrigger(550, () -> {
                joinScene.launchCamera(4, 400, 20); // 30
            });
            joinScene.createTickTrigger(580, () -> {
                joinScene.getCamera(4).setWatcher(player);
                joinScene.getCamera(3).setWatcher(null);
                joinScene.removeCamera(3);
            });

            joinScene.createTickTrigger(740, () -> {
                joinScene.getCamera(4).setTarget(npcs[0].get(4));
            });

            joinScene.createTickTrigger(760, () -> {
                joinScene.launchCamera(5, 400, 20); // 30
                joinScene.getCamera(5).setTarget(joinScene.getSceneWatcher().getWatcherFakePlayer());
            });

            joinScene.createTickTrigger(840, () -> {
                joinScene.getCamera(5).setWatcher(player);
                joinScene.getCamera(4).setWatcher(null);
                joinScene.removeCamera(4);
            });

            joinScene.executeAtEnd(() -> {
                joinScene.getSceneWatcher().removeFakePlayer();
                final Player watcher = joinScene.getSceneWatcher().getWatcher();
                watcher.teleport(joinScene.getSceneWatcher().getWatcherLastLocation());

                watcher.sendMessage("Добро пожаловать!");
                joinScene.removeSceneWatcher();
                for (int number : npcs[0].keySet()) {
                    Mannequin npc = npcs[0].get(number);
                    if (npc == null) continue;
                    ScriptedMovement move = scriptMoveManager.getScriptedMovementFromEntity(npc);
                    if (move != null) {
                        move.stop(npc, true);
                        move.clearAll();
                        scriptMoveManager.getMobPaths().remove(npc.getUniqueId());

                    }
                    npc.remove();
                }
                Mannequin startNpcMovable = (Mannequin) player.getWorld().spawnEntity(new Location(player.getWorld(), -874.1635449788745, 67.0, -1575.9611873962851, -89.8F, 0.0F), EntityType.MANNEQUIN);
                startNpcMovable.setImmovable(true);
                startNpcMovable.setInvulnerable(true);
                startNpcMovable.setRemoveWhenFarAway(true);
                ScriptedMovement pathFollow = new ScriptedMovement();
                pathFollow.setRubberTarget(player, true);
                pathFollow.setRubberNearDistance(4, 2);
                pathFollow.setRubberFarDistance(12, 16);
                try {
                    scriptMoveManager.loadPathFromFile(player.getWorld(), pathFollow, "c1_fpath1.yml");
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                scriptMoveManager.getMobPaths().put(startNpcMovable.getUniqueId(), pathFollow);
                ctxManager.moveToContext(startNpcMovable, privateStartContext);
                pathFollow.runReplayRecording(startNpcMovable, 0, true, () -> {
                    pathFollow.clearAll();
                    scriptMoveManager.getMobPaths().remove(startNpcMovable.getUniqueId());
                    if (startNpcMovable.isValid()) {
                        startNpcMovable.remove();
                    }
                });
            });

            joinScene.runScene();


        }, 20L);
    }

    @EventHandler
    public void changeSpec(PlayerStopSpectatingEntityEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = PLAYERS.get(player.getUniqueId());
        if (playerData == null) return;
        if (playerData.isInCutscene()) e.setCancelled(true);
    }


    @EventHandler
    public void sneak(PlayerToggleSneakEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = PLAYERS.get(player.getUniqueId());
        if (playerData == null) return;
        if (playerData.isInCutscene()) e.setCancelled(true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = PLAYERS.get(player.getUniqueId());
        if (playerData == null) return;
        if (playerData.isInCutscene()) e.setCancelled(true);
    }

    @EventHandler
    public void interactEntity(PlayerInteractEntityEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = PLAYERS.get(player.getUniqueId());
        if (playerData == null) return;
        if (playerData.isInCutscene()) e.setCancelled(true);
        Map<Player, LivingEntity> targetMap = Main.tweakManager.getCameraManager().getPlayerCameraCreationTarget();
        if (targetMap.containsKey(player) && targetMap.get(player) == null) {
            targetMap.put(player, (LivingEntity) e.getRightClicked());
            player.sendMessage("Выбрана сущность: " + e.getRightClicked().getUniqueId());
        }
    }

}
