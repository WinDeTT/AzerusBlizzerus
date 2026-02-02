package org.windett.azerusBlizzerus;

import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.utils.cutscene.camera.CameraManager;
import org.windett.azerusBlizzerus.utils.pathRecorder.ScriptMoveManager;

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

    private final CameraManager cameraManager;
    private final ScriptMoveManager scriptMoveManager;
    private final ContextManager contextManager;

    public TweakManager() {
        cameraManager = new CameraManager();
        scriptMoveManager = new ScriptMoveManager();
        contextManager = new ContextManager();

    }
}
