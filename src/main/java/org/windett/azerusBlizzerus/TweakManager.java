package org.windett.azerusBlizzerus;

import org.bukkit.Bukkit;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.events.listeners.util.camera.CameraListener;
import org.windett.azerusBlizzerus.events.listeners.util.regionzone.RegionZoneListener;
import org.windett.azerusBlizzerus.utils.cutscene.camera.CameraManager;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptMoveManager;
import org.windett.azerusBlizzerus.utils.regionzone.RegionZoneManager;

public class TweakManager {


    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ScriptMoveManager getScriptMoveManager() {
        return scriptMoveManager;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public RegionZoneManager getRegionZoneManager() {
        return regionZoneManager;
    }

    public CameraListener getCameraListener() {
        return cameraListener;
    }

    private final CameraManager cameraManager;
    private final ScriptMoveManager scriptMoveManager;
    private final ContextManager contextManager;


    private final RegionZoneManager regionZoneManager;
    private final RegionZoneListener regionZoneListener;

    private final CameraListener cameraListener;

    public TweakManager() {
        cameraManager = new CameraManager();
        scriptMoveManager = new ScriptMoveManager();
        contextManager = new ContextManager();
        regionZoneManager = new RegionZoneManager();

        regionZoneListener = new RegionZoneListener();
        cameraListener = new CameraListener();

        Bukkit.getPluginManager().registerEvents(regionZoneListener, Main.instance);
        Bukkit.getPluginManager().registerEvents(cameraListener, Main.instance);

    }
}
