package org.windett.azerusBlizzerus.utils.cutscene.camera;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.windett.azerusBlizzerus.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class  CameraManager {

    public List<Camera> getWorkingCameras() {
        return workingCameras;
    }

    public List<Mannequin> getFakePlayerModels() {
        return fakePlayerModels;
    }

    public Map<Player, List<Location>> getPlayerCameraCreation() {
        return playerCameraCreation;
    }

    public Map<Player, LivingEntity> getPlayerCameraCreationTarget() {
        return playerCameraCreationTarget;
    }

    private final List<Camera> workingCameras = new ArrayList<>();
    private final List<Mannequin> fakePlayerModels = new ArrayList<>();

    private final  Map<Player, List<Location>> playerCameraCreation = new HashMap<>();
    private final Map<Player, LivingEntity> playerCameraCreationTarget = new HashMap<>();

    public Camera findCameraByCameraEntity(Entity check) {
        if (check.getType() != EntityType.ITEM_DISPLAY) return null;
        for (Camera camera : workingCameras) {
            if (camera.getCameraEntity().equals(check)) {
                return camera;
            }
        }
        return null;
    }

    public CameraManager() {

    }

    public List<Location> loadCameraPoints(World world, String fileName) throws FileNotFoundException {
        if (Main.cameraRecsFolder == null || !Main.cameraRecsFolder.exists()) {
            Bukkit.getLogger().info("cameraRecsFolder is not exists. CameraManager loadCameraPoints method");
            return null;
        }
        if (!fileName.endsWith(".yml")) {
            Bukkit.getLogger().info("Incorrect file format. Needs YML. CameraManager loadCameraPoints method");
            return null;
        }
        List<Location> points = new ArrayList<>();
        final File savedFile = new File(Main.cameraRecsFolder, fileName);
        if (!savedFile.exists()) {
            Bukkit.getLogger().info("File with name " + fileName + " is not exists. CameraManager loadCameraPoints method.");
            return null;
        }
        Scanner scanner = new Scanner(savedFile);
        scanner.useLocale(Locale.US);
        while (scanner.hasNextLine()) {
            String[] sub = scanner.nextLine().split(" ");
            double x = Double.parseDouble(sub[0]);
            double y = Double.parseDouble(sub[1]);
            double z = Double.parseDouble(sub[2]);
            float yaw = Float.parseFloat(sub[3]);
            float pitch = Float.parseFloat(sub[4]);
            Location cameraLoc = new Location(world, x,y,z,yaw,pitch);
            points.add(cameraLoc);
        }
        scanner.close();
        if (points.isEmpty()) {
            Bukkit.getLogger().info("File is empty. CameraManager loadCameraPoints method");
            return null;
        }
        return points;
    }
}
