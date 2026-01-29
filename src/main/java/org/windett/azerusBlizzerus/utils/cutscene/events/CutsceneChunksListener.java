package org.windett.azerusBlizzerus.utils.cutscene.events;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.windett.azerusBlizzerus.utils.cutscene.camera.CameraManager;

import java.util.Arrays;

public class CutsceneChunksListener implements Listener {

    @EventHandler
    public void atChunkLoad(ChunkLoadEvent e) {

        final Chunk chunk = e.getChunk();
        Arrays.stream(chunk.getEntities()).filter(CameraManager.fakePlayerModels::equals).forEach(
                Entity::remove
        );
    }
}
