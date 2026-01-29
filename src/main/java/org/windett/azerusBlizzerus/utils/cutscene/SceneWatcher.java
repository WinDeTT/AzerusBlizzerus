package org.windett.azerusBlizzerus.utils.cutscene;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SceneWatcher {

    private final @NotNull Player watcher;
    private final @NotNull Location watcherLastLocation;
    private Mannequin watcherFakePlayer;

    public SceneWatcher(Player watcher, boolean createFakePlayerModel) {
        this.watcher = watcher;
        this.watcherLastLocation = watcher.getLocation();
        if (createFakePlayerModel) createFakePlayer();
    }

    public @NotNull Player getWatcher() {
        return watcher;
    }

    public @NotNull Location getWatcherLastLocation() {
        return watcherLastLocation;
    }

    public Mannequin getWatcherFakePlayer() {
        return watcherFakePlayer;
    }

    public void createFakePlayer() {
        final Mannequin fake = (Mannequin) watcherLastLocation.getWorld().spawnEntity(watcherLastLocation, EntityType.MANNEQUIN);
        fake.setProfile(ResolvableProfile.resolvableProfile(watcher.getPlayerProfile()));
        fake.setAI(false);
        fake.setImmovable(true);
        fake.setInvulnerable(true);
        fake.setCustomName(watcher.getName());
        this.watcherFakePlayer = fake;
    }

    public void removeFakePlayer() {
        if (watcherFakePlayer == null) return;
        if (watcherFakePlayer.isDead() || !watcherFakePlayer.isValid()) return;
        watcherFakePlayer.remove();
        watcherFakePlayer = null;
    }

    public boolean fakePlayerExists() {
        return watcherFakePlayer != null && (watcherFakePlayer.isValid() || !watcherFakePlayer.isDead());
    }
}
