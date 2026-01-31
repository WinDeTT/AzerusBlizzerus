package org.windett.azerusBlizzerus.utils.pathRecorder;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.windett.azerusBlizzerus.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ScriptMoveManager {
    public Map<UUID, ScriptedMovement> getMobPaths() {
        return mobPaths;
    }

    private final Map<UUID, ScriptedMovement> mobPaths = new HashMap<>();

    public ScriptMoveManager() {

    }

    public ScriptedMovement getScriptedMovementFromEntity(Entity entity) {
        if (!entity.isValid()) {
            if (mobPaths.containsKey(entity.getUniqueId())) mobPaths.remove(entity.getUniqueId());
        }
        if (!mobPaths.containsKey(entity.getUniqueId())) return null;
        return mobPaths.get(entity.getUniqueId());
    }

    public void saveRecordingToFile(Player player, ScriptedMovement record, String fileName) {
        if (Main.pathRecsFolder == null) {
            player.sendMessage(Component.text("Ошибка. Директории PathRecords не существует!"));
            return;
        }
        if (record == null) return;
        if (record.isEmpty()) return;
        String name = fileName.toLowerCase();
        if (name.length() > 16) {
            player.sendMessage(Component.text("Ошибка. Имя файла слишком длинное!").color(TextColor.color(255,20,60)));
            return;
        }
        if (!name.endsWith(".yml")) {
            player.sendMessage(Component.text("Ошибка! Файл должен иметь формат YML").color(TextColor.color(255,20,60)));
            return;
        }

        final Location startLoc = record.getStartLocation();
        StringBuilder sb = new StringBuilder();
        sb.append(startLoc.getX() + " " + startLoc.getY() + " " + startLoc.getZ() + " " + startLoc.getYaw() + " " + startLoc.getPitch());
        Vector offset;
        String yawRot;
        String pitchRot;
        double vecX;
        double vecY;
        double vecZ;
        String vecOffsetStr;
        boolean wasClickedLeft;
        boolean wasClickedRight;
        for (int index = 0; index < record.getPathList().size() ; index++) {
            offset = record.getPathList().get(index).getLocOffset();
            vecX = offset.getX();
            vecY = offset.getY();
            vecZ = offset.getZ();
            vecOffsetStr = Math.round(vecX * 100.000000) / 100.000000 + "#" + Math.round(vecY * 100.000000) / 100.000000 + "#" + Math.round(vecZ * 100.000000) / 100.000000;
            yawRot = record.getPathList().get(index).getRotation().first().toString();
            pitchRot = record.getPathList().get(index).getRotation().second().toString();
            wasClickedLeft = record.getPathList().get(index).getSwingHands().first();
            wasClickedRight = record.getPathList().get(index).getSwingHands().second();
            sb.append("\n" + vecOffsetStr + " " + yawRot + " " + pitchRot + " " + wasClickedLeft + " " + wasClickedRight);
        }

        File saveFile = new File(Main.pathRecsFolder, fileName);
        try {
            FileUtils.writeStringToFile(saveFile, sb.toString(), StandardCharsets.UTF_8);
            player.sendMessage("Файл успешно сохранён! " + saveFile.getPath());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void loadPathFromFile(World world, ScriptedMovement movement, String fileName) throws FileNotFoundException {
        if (!fileName.endsWith(".yml")) {
            Bukkit.getLogger().info("Ошибка! Указан неверный формат файла!");
            return;
        }
        File file = new File(Main.pathRecsFolder, fileName);
        if (!file.exists()) {
            Bukkit.getLogger().info("Ошибка! Файл " + fileName + " не существует!");
            return;
        }
        Scanner scanner = new Scanner(file);
        scanner.useLocale(Locale.US);

        String line = scanner.nextLine();
        if (line == null) {
            Bukkit.getLogger().info("Файл " + fileName + " пуст!");
            scanner.close();
            return;
        }
        movement.getPathList().clear();
        String[] splitter = line.split(" ");
        double x = Double.parseDouble(splitter[0]);
        double y = Double.parseDouble(splitter[1]);
        double z = Double.parseDouble(splitter[2]);
        float yaw = Float.parseFloat(splitter[3]);
        float pitch = Float.parseFloat(splitter[4]);


        final Location startLoc = new Location(world, x,y,z,yaw,pitch);
        movement.setStartLocation(startLoc);
        Vector offset;
        double vecX;
        double vecY;
        double vecZ;
        String offsetStr;
        Pair<Float, Float> rotation;
        String wasClickedLeft;
        String wasClickedRight;
        Pair<Boolean, Boolean> handSwings;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            splitter = line.split(" ");
            offsetStr = splitter[0];
            yaw = Float.parseFloat(splitter[1]);
            pitch = Float.parseFloat(splitter[2]);
            rotation = Pair.of(yaw, pitch);
            boolean leftClick = false;
            boolean rightClick = false;
            if (splitter.length > 3) {
                wasClickedLeft = splitter[3];
                wasClickedRight = splitter[4];
                leftClick = Boolean.parseBoolean(wasClickedLeft);
                rightClick = Boolean.parseBoolean(wasClickedRight);

            }
            handSwings = Pair.of(leftClick, rightClick);
            splitter = offsetStr.split("#");
            vecX = Double.parseDouble(splitter[0]);
            vecY = Double.parseDouble(splitter[1]);
            vecZ = Double.parseDouble(splitter[2]);
            offset = new Vector(vecX, vecY, vecZ);
            movement.getPathList().add(new PathTickData(offset, rotation, Pair.of(false, false)));
        }
        scanner.close();
        Bukkit.getLogger().info("Загрузка пути " + fileName + " прошла успешно!");
    }

    public Vector rotateVector(Vector vector, float yaw, float pitch) {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        double newX = x * Math.cos(radYaw) - z * Math.sin(radYaw);
        double newZ = x * Math.sin(radYaw) + z * Math.cos(radYaw);

        double newY = y * Math.cos(radPitch) - newZ * Math.sin(radPitch);
        newZ = y * Math.sin(radPitch) + newZ * Math.cos(radPitch);

        return new Vector(newX, newY, newZ);
    }
}
