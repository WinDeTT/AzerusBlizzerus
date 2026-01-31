package org.windett.azerusBlizzerus.command.camera;

import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.utils.cutscene.camera.Camera;
import org.windett.azerusBlizzerus.utils.cutscene.camera.CameraManager;
import org.windett.azerusBlizzerus.utils.cutscene.camera.Cutscene_t1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CameraCommand extends BukkitCommand {

    private final CameraManager cameraManager = Main.tweakManager.getCameraManager();

    public CameraCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
        setPermission("plugin.camera");
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
        final Location playerEyeLocation = player.getEyeLocation().add(0, -0.5, 0);
        List<Location> cameraFlightPoints = cameraManager.getPlayerCameraCreation().get(player);
        LivingEntity targetEntity = cameraManager.getPlayerCameraCreationTarget().getOrDefault(player, null);
        switch (args[0]) {
            case "create" -> {
                cameraManager.getPlayerCameraCreation().put(player, new ArrayList<>());
                player.sendMessage(Component.text("Вы создали новую камеру."));
            }
            case "add" -> {
                if (cameraFlightPoints == null) {
                    return true;
                }
                cameraFlightPoints.add(playerEyeLocation.clone());
                String pointAddingMessage = "Добавлена локация " + cameraFlightPoints.size();
                if (cameraFlightPoints.size() == 1)
                    pointAddingMessage = pointAddingMessage + " (Начальная точка)";
                player.sendMessage(Component.text(pointAddingMessage));
            }
            case "tp" -> {
                if (cameraFlightPoints.isEmpty()) {
                    player.sendMessage(Component.text("Ваша камера не имеет ни одной точки."));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: /camera tp <pointID> (1-" + cameraFlightPoints.size() + ")"));
                    return true;
                }
                int pointId;
                try {
                    pointId = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("Вы должны указать число: /camera tp <pointId>"));
                    return true;
                }
                if (pointId < 1 || pointId > cameraFlightPoints.size()) {
                    player.sendMessage(Component.text("Ошибка! Указанное число выходит за границы допустимого диапазона!"));
                    player.sendMessage(Component.text("Использование: /camera tp <pointID> (1-" + cameraFlightPoints.size() + ")"));
                    return true;
                }
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(cameraFlightPoints.get(pointId - 1));
            }
            case "replace" -> {
                if (cameraFlightPoints.isEmpty()) {
                    player.sendMessage(Component.text("Ваша камера не имеет ни одной точки."));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: /camera replace <pointId>"));
                    return true;
                }
                int pointId = 0;
                try {
                    pointId = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("Использование: /camera replace <pointId> (1-" + cameraFlightPoints.size() + ")"));
                    return true;
                }
                cameraFlightPoints.set(pointId - 1, playerEyeLocation);
                player.sendMessage(Component.text("Точка камеры " + pointId + " была обновлена."));
            }
            case "clear" -> {
                if (!cameraFlightPoints.isEmpty()) {
                    cameraFlightPoints.clear();
                    player.sendMessage(Component.text("Камера очищена."));
                } else player.sendMessage(Component.text("Камера и так пустая."));
            }
            case "start" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: /camera start <ticks> <interpolation>"));
                    return true;
                }
                int time = 0;
                int interpol = 20;
                try {
                    time = Integer.parseInt(args[1]);
                    if (time < 5) {
                        player.sendMessage(Component.text("Укажите число больше, чем 5 arg<> {ticks}"));
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("Указано неверное значение arg<> {ticks}"));
                    return true;
                }
                if (args.length > 2) {
                    try {
                        interpol = Integer.parseInt(args[2]);
                        if (interpol < 1 || interpol > 59) {
                            player.sendMessage(Component.text("Указано некорректное время интерполяции (arg3)"));
                            interpol = 20;
                        }
                    } catch (NumberFormatException ex) {
                        interpol = 20;
                    }
                }
                List<Location> readyCameraPoints = new ArrayList<>();
                for (Location location : cameraFlightPoints) {
                    readyCameraPoints.add(location.clone());
                }
                Cutscene_t1.startRecordedCameraFlying(player, readyCameraPoints, time, interpol, targetEntity);
            }
            case "stop" -> {
                if (player.getGameMode() != GameMode.SPECTATOR) return true;
                Entity spectated = player.getSpectatorTarget();
                if (spectated == null) return true;
                if (spectated.getType() != EntityType.ITEM_DISPLAY) return true;
                final Camera camera = cameraManager.findCameraByCameraEntity(spectated);
                if (camera == null) return true;
                camera.stopCamera();
                camera.remove();
            }
            case "target" -> {
                Map<Player, LivingEntity> cameraTarget = cameraManager.getPlayerCameraCreationTarget();
                if (args.length < 2) {
                    cameraTarget.put(player, null);
                    player.sendMessage(Component.text("Кликните по любой сущности, чтобы направить взгляд камеры на неё."));
                    return true;
                } else if (!args[1].equals("self") && !args[1].equals("clear")) {
                    player.sendMessage(Component.text("Использование: /camera target [self | clear]"));
                    return true;
                }
                if (args[1].equals("self")) {
                    cameraTarget.put(player, player);
                    player.sendMessage(Component.text("Вы установили себя как цель!"));
                } else {
                    cameraTarget.remove(player);
                    player.sendMessage(Component.text("Цель для камеры очищена!"));
                }
            }
            case "save" -> {
                if (Main.cameraRecsFolder == null || !Main.cameraRecsFolder.exists()) {
                    Bukkit.getLogger().info("Папка cameraRecsFolder не существует. <CameraCommand/execute/case -> save>");
                    return true;
                }
                if (cameraFlightPoints.isEmpty()) {
                    player.sendMessage(Component.text("Камера не имеет ни одной точки."));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Укажите название файла: camera save <fileName.yml>"));
                    return true;
                }
                String name = args[1].toLowerCase().trim();
                if (name.length() > 20) {
                    player.sendMessage(Component.text("Длинна названия файла слишком большая."));
                    return true;
                }
                if (!name.endsWith(".yml")) {
                    player.sendMessage(Component.text("Файл должен иметь формат .yml"));
                    return true;
                }
                final File savedCamera = new File(Main.cameraRecsFolder, name);
                StringBuilder sb = new StringBuilder();
                for (Location location : cameraFlightPoints) {
                    double x = location.getX();
                    double y = location.getY();
                    double z = location.getZ();
                    float yaw = location.getYaw();
                    float pitch = location.getPitch();

                    String appendedLine = x + " " + y + " " + z + " " + yaw + " " + pitch;
                    if (!sb.isEmpty()) appendedLine = "\n" + appendedLine;
                    sb.append(appendedLine);
                }

                try {
                    FileUtils.writeStringToFile(savedCamera, sb.toString(), StandardCharsets.UTF_8);
                    player.sendMessage(Component.text("Файл успешно сохранён! " + savedCamera.getPath()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            case "load" -> {
                if (Main.cameraRecsFolder == null || !Main.cameraRecsFolder.exists()) {
                    Bukkit.getLogger().info("Папка cameraRecsFolder не существует. <CameraCommand/execute/case -> load>");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Использование: camera load <fileName.yml>"));
                    return true;
                }
                String name = args[1];
                final List<Location> loadPoints;
                try {
                    loadPoints = cameraManager.loadCameraPoints(player.getWorld(), name);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (loadPoints == null) {
                    player.sendMessage(Component.text("Ошибка при загрузке файла. Причина ошибки в консоли."));
                    return true;
                }
                cameraManager.getPlayerCameraCreation().put(player, loadPoints);
                player.sendMessage(Component.text("Камера " + name + " успешно загружена!"));
            }
            case "list" -> {
                final File cameraRecs = Main.cameraRecsFolder;
                if (cameraRecs == null || !cameraRecs.exists()) {
                    Bukkit.getLogger().info("Папка cameraRecsFolder не существует. Вызвано из PlayerJoinQuitListener / camera list method.");
                    return true;
                }
                List<File> files = List.of(Objects.requireNonNull(cameraRecs.listFiles()));
                if (files.isEmpty()) {
                    player.sendMessage("Не найдено ни одного файла записи камеры.");
                    return true;
                }
                for (File file : files) {
                    if (!file.getName().endsWith(".yml")) continue;
                    player.sendMessage("- " + file.getName());
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender,
                                             @NotNull String alias,
                                             @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        List<Location> cameraFlyPoints = cameraManager.getPlayerCameraCreation().getOrDefault(player, null);
        if (args.length == 1) {
            if (cameraFlyPoints == null) {
                return Arrays.asList("create", "list", "load");
            }
            if (cameraFlyPoints.isEmpty()) {
                return Arrays.asList("create", "add", "list", "load");
            }
            return Arrays.asList("create", "add", "tp", "replace", "clear", "start", "target", "stop", "save", "load", "list");
        }
        switch (args[0]) {
            case "tp", "replace" -> {
                if (cameraFlyPoints == null || cameraFlyPoints.isEmpty()) return List.of();
                if (cameraFlyPoints.size() < 2) return List.of("1");
                int index = 0;
                List<String> availableValues = new ArrayList<>();
                while (index < cameraFlyPoints.size()) {
                    availableValues.add(String.valueOf(index + 1));
                    index++;
                }
                return availableValues;
            }
            case "start" -> {
                if (cameraFlyPoints == null || cameraFlyPoints.isEmpty()) return List.of();
                if (args.length == 2) return List.of("400");
                if (args.length == 3) return List.of("20");
            }
            case "target" -> {
                if (cameraFlyPoints == null) return List.of();
                return Arrays.asList("self", "clear");
            }
            case "save" -> {
                return List.of("testCamera.yml");
            }
            case "load" -> {
                final File cameraRecs = Main.cameraRecsFolder;
                if (cameraRecs == null || !cameraRecs.exists()) {
                    return List.of();
                }
                List<File> files = List.of(Objects.requireNonNull(cameraRecs.listFiles()));
                if (files.isEmpty()) {
                    return List.of();
                }
                List<String> fileNames = new ArrayList<>();
                for (File file : files) {
                    if (!file.getName().endsWith(".yml")) continue;
                    fileNames.add(file.getName());
                }
                return fileNames;
            }
        }

        return List.of();
    }


    public List<String> usageList = List.of(
            "==РАБОТА С КАМЕРОЙ==",
            "/camera create - создать новую камеру",
            "/camera add - добавить новую точку пролёта камеры",
            "(будет взята ваша текущая позиция)",
            "/camera tp <pointID> - телепортироваться на выбранную точку",
            "/camera replace <pointId> - заменить точку в списке",
            "(будет взята ваша текущая позиция)",
            "/camera clear - очистить точки пролёта камеры",
            "/camera start <ticks> <interpolation> - запустить пролёт камеры",
            "(<ticks> - время, выделенное под все точки, <interpolation> - плавность движения [1-59])",
            "/camera target | /camera target {self/clear} - установить взгляд камеры на сущность/себя или открепить",
            "/camera stop - остановить камеру и вернуть наблюдателя в исходную позицию",
            "/camera save <name.yml> - сохранить точки движения камеры в файл",
            "/camera load <name.yml> - загрузить точки движения камеры из файла"
    );
}
