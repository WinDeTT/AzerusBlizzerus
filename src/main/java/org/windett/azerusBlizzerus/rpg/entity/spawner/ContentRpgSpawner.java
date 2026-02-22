package org.windett.azerusBlizzerus.rpg.entity.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.content.ContentRpgEntity;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.context.WorldContext;
import org.windett.azerusBlizzerus.rpg.entity.RpgMob;
import org.windett.azerusBlizzerus.rpg.entity.RpgPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContentRpgSpawner {

    private final UUID spawnerID;
    private final String world;
    private final String context;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final int spawnCooldown;
    private int spawnTimer;
    private final double leashRange;
    private final ContentRpgEntity contentMob;
    private LivingEntity entityMob;
    private boolean killed;
    private BukkitTask spawnerRunnable = null;

    public ContentRpgSpawner(Builder builder) {
        this.spawnerID = UUID.randomUUID();
        this.world = builder.world;
        this.context = builder.context;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
        this.spawnCooldown = builder.spawnCooldown;
        this.leashRange = builder.leashRange;
        this.contentMob = builder.contentMob;
        this.killed = true;
        this.spawnTimer = 0;
        final World world = Bukkit.getWorld(this.world);
        if (world == null) {
            return;
        }
        if (contentMob == null) {
            return;
        }
        Main.rpgSystemManager.getRpgEntityManager().getRpgMobSpawnerMap().put(this.spawnerID, this);
        if (Main.tweakManager.getContextManager().isContextRunning(context)) {
            startSpawnPointWork();
        }
    }

    public static class Builder {
        private String world = "world";
        private String context = ContextManager.GLOBAL_CONTEXT;
        private double x = 0.0;
        private double y = 0.0;
        private double z = 0.0;
        private float yaw = 0.0F;
        private float pitch = 0.0F;
        private int spawnCooldown = 20;
        private double leashRange = 30.0;
        private ContentRpgEntity contentMob = null;

        public Builder world(String world) {
            this.world = world;
            return this;
        }

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        public Builder position(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }

        public Builder cooldown(int cooldown) {
            this.spawnCooldown = cooldown;
            return this;
        }

        public Builder leashRange(double leashRange) {
            this.leashRange = leashRange;
            return this;
        }

        public Builder mobId(int id) throws IllegalArgumentException {
            ContentRpgEntity cte = Main.rpgSystemManager.getRpgEntityManager()
                    .getContentMobMap().get(id);
            if (cte == null) {
                throw new IllegalArgumentException("Моб с ID " + id + " не найден");
            }
            this.contentMob = cte;
            return this;
        }

        public ContentRpgSpawner build() {
            return new ContentRpgSpawner(this);
        }
    }

    public String getWorldName() {
        return world;
    }
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public String getContext() {
        return context;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public int getSpawnCooldown() {
        return spawnCooldown;
    }

    public ContentRpgEntity getContentMob() {
        return contentMob;
    }

    public LivingEntity getEntityMob() {
        return entityMob;
    }

    public void setKilled() {
        killed = true;
        spawnTimer = spawnCooldown;
    }

    public boolean isKilled() {
        return killed;
    }

    public BukkitTask getSpawnerRunnable() {
        return spawnerRunnable;
    }

    private boolean handleRespawn(Location spawnLocation) {
        World world = getWorld();
        int X = spawnLocation.getBlockX() >> 4;
        int Z = spawnLocation.getBlockZ() >> 4;
        if (world.isChunkLoaded(X, Z)) {
            if (!hasNearbyPlayers(spawnLocation)) return false;
            entityMob = (LivingEntity) Main.rpgSystemManager.getRpgEntityManager().spawnRpgEntity(context, contentMob.getId(), spawnLocation, ContentRpgSpawner.this);
            killed = false;
            return true;
        } else return false;
    }


    private void handleTeleportLeashBackIf(boolean wentFar) {
        if (!wentFar) return;
        CraftEntity ce = (CraftEntity) entityMob;
        ce.getHandle().setPos(x, y, z);
        if (!(entityMob instanceof Creature)) return;
        ((Creature) entityMob).setTarget(null);
        RpgMob rm = (RpgMob) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(entityMob);
        // далее...
    }

    private void handleDespawnIfHasNotNearbyPlayers(boolean hasNearbyPlayers) {
        if (hasNearbyPlayers) return;
        entityMob.remove();
    }

    private boolean hasNearbyPlayers(Location location) {
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player player : location.getNearbyPlayers(50,50,50)) {
            RpgPlayer rpgPlayer = (RpgPlayer) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(player);
            if (rpgPlayer == null) continue;
            WorldContext playerCtx = Main.tweakManager.getContextManager().getEntityContext(player);
            if (playerCtx.getContextName().equals(context)) nearbyPlayers.add(player);
        }
        return !nearbyPlayers.isEmpty();
    }

    public void startSpawnPointWork() {
        if (spawnerRunnable != null && !spawnerRunnable.isCancelled()) return;
        final World world = getWorld();
        final Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        this.spawnerRunnable = new BukkitRunnable() {

            public void run() {
                if (entityMob != null && entityMob.isValid()) {
                    handleDespawnIfHasNotNearbyPlayers(hasNearbyPlayers(entityMob.getLocation()));
                    handleTeleportLeashBackIf(entityMob.getLocation().distanceSquared(spawnLocation) >= leashRange * leashRange);
                    return;
                }
                if (killed) {
                    if (spawnTimer < 1) {
                        if (handleRespawn(spawnLocation)) {
                            spawnTimer = spawnCooldown;
                        }
                    } else {
                        spawnTimer--;
                    }
                } else {
                    killed = true;
                    spawnTimer = 0;
                }
            }
        }.runTaskTimer(Main.instance, 20L, 20L);
    }

    public void stopSpawnPointWork() {
        if (spawnerRunnable != null) {
            if (!spawnerRunnable.isCancelled()) spawnerRunnable.cancel();
        }
        spawnerRunnable = null;
        if (entityMob != null) {
            if (entityMob.isValid()) {
                entityMob.remove();
            }
        }
        entityMob = null;
        killed = true;
    }
}
