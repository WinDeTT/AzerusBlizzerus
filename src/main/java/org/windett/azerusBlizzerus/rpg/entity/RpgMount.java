package org.windett.azerusBlizzerus.rpg.entity;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;
import org.windett.azerusBlizzerus.context.ContextManager;
import org.windett.azerusBlizzerus.context.WorldContext;

import java.util.UUID;

public class RpgMount implements RpgEntity {

    final ContextManager ctxManager = Main.tweakManager.getContextManager();

    public static NamespacedKey getMountKey() {
        return mountKey;
    }
    private static final NamespacedKey mountKey = new NamespacedKey(Main.instance, "isMount");
    private final Horse mount;
    private String context;


    public RpgMount(@NotNull RpgPlayer rpgPlayer, float speed) {
        Player player = (Player) rpgPlayer.asBukkitEntity();
        Horse mount = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        mount.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
        initMount(mount);
        WorldContext context = ctxManager.getContextMap().get(rpgPlayer.getContext());
        if (context != null) {
            ctxManager.moveToContext(mount, context.getContextName());
            this.context = context.getContextName();
        }
        else {
            this.context = ctxManager.getGlobalContext().getContextName();
        }
        mount.setOwner(player);
        mount.addPassenger(player);
        this.mount = mount;
        Main.rpgSystemManager.getRpgEntityManager().registerRpgEntity(mount.getUniqueId(), this);
        rpgPlayer.setMount(this);
    }

    public void initMount(Horse mount) {
        mount.setInvulnerable(true);
        mount.setTamed(true);
        mount.getPersistentDataContainer().set(mountKey, PersistentDataType.BOOLEAN, true);
    }

    public Horse getMount() {
        return mount;
    }

    public void remove() {
        if (mount != null && !mount.isDead()) mount.remove();
    }

    @Override
    public UUID getUniqueId() {
        return mount.getUniqueId();
    }

    @Override
    public Entity asBukkitEntity() {
        return mount;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public World getWorld() {
        return mount.getWorld();
    }

    @Override
    public Location getLocation() {
        return mount.getLocation();
    }

    @Override
    public boolean isValid() {
        return mount != null && mount.isValid();
    }
}
