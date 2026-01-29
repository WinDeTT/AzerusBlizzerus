package org.windett.azerusBlizzerus;

import java.io.File;
import java.io.IOException;

public class ServerFile {

    public File getPluginFolder() {
        return pluginFolder;
    }

    private final File pluginFolder;

    public File getCameraRecsFolder() {
        return cameraRecsFolder;
    }

    private final File cameraRecsFolder;

    public File getPathRecsFolder() {
        return pathRecsFolder;
    }

    private final File pathRecsFolder;

    public ServerFile() throws IOException {
        pluginFolder = Main.instance.getDataFolder();
        logCreation("pluginFolder", pluginFolder);

        cameraRecsFolder = new File(pluginFolder, "CameraRecords");
        logCreation("cameraRecsFolder", cameraRecsFolder);

        pathRecsFolder = new File(pluginFolder, "PathRecords");
        logCreation("pathRecsFolder", pathRecsFolder);
    }


    private void logCreation(String name, File folder) throws IOException {
        Main.instance.getLogger().info("Создание " + name + ": " + folder.getAbsolutePath());
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Не удалось создать " + name + " в " + folder.getAbsolutePath());
        }
    }
}
