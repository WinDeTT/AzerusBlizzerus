package org.windett.azerusBlizzerus.content.contentBase;

import org.windett.azerusBlizzerus.rpg.entity.spawner.ContentRpgSpawner;

public class RpgSpawnerBase {

    public RpgSpawnerBase() {
        ContentRpgSpawner spawner = new ContentRpgSpawner.Builder()
                .context("global")
                .world("world")
                .position(-869.284, 67.0, -1575.749, 90.0F, 0.0F)
                .leashRange(40)
                .cooldown(30)
                .mobId(1)
                .build();
    }
}
