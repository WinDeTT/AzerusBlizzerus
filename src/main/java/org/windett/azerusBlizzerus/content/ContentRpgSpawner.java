package org.windett.azerusBlizzerus.content;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.rpg.entity.RpgMob;

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
        Bukkit.broadcastMessage("0");
        final World world = Bukkit.getWorld(this.world);
        if (world == null) {
            return;
        }
        Bukkit.broadcastMessage("1");
        if (!Main.tweakManager.getContextManager().getContextMap().containsKey(context)) {
            return;
        }
        Bukkit.broadcastMessage("2");
        if (contentMob == null) {
            return;
        }
        Bukkit.broadcastMessage("3");
        Main.rpgSystemManager.getRpgEntityManager().getRpgMobSpawnerMap().put(this.spawnerID, this);

        final Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        this.spawnerRunnable = new BukkitRunnable() {
            int cooldown = 0;

            public void run() {
                if (entityMob != null && entityMob.isValid()) {
                    if (entityMob.getLocation().distanceSquared(spawnLocation) >= leashRange * leashRange) {
                        CraftEntity ce = (CraftEntity) entityMob;
                        ce.getHandle().setPos(x,y,z);
                        if (entityMob instanceof Creature) {
                            ((Creature) entityMob).setTarget(null);
                            RpgMob rm = (RpgMob) Main.rpgSystemManager.getRpgEntityManager().asRpgMob(entityMob);
                            // далее...
                        }
                    }
                    return;
                }
                if (killed) {
                    if (cooldown < 1) {
                        int X = spawnLocation.getBlockX() >> 4;
                        int Z = spawnLocation.getBlockZ() >> 4;
                        if (world.isChunkLoaded(X, Z)) {
                            entityMob = (LivingEntity) Main.rpgSystemManager.getRpgEntityManager().spawnRpgEntity(context, contentMob.getId(), spawnLocation, ContentRpgSpawner.this);
                            killed = false;
                            cooldown = spawnCooldown;
                        }
                    } else {
                        cooldown--;
                    }
                } else {
                    killed = true;
                    cooldown = 0;
                }
            }
        }.runTaskTimer(Main.instance, 20L, 20L);
    }

    public static class Builder {
        private String world = "world";
        private String context = Main.tweakManager.getContextManager().getGlobalContext().getContextName();
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

    public boolean isKilled() {
        return killed;
    }

    public BukkitTask getSpawnerRunnable() {
        return spawnerRunnable;
    }
}
