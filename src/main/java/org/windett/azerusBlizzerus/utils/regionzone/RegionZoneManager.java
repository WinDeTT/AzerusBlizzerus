package org.windett.azerusBlizzerus.utils.regionzone;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.events.custom.regionzone.RegionZoneJoinEvent;
import org.windett.azerusBlizzerus.events.custom.regionzone.RegionZoneLeaveEvent;

import java.util.*;

public class RegionZoneManager {

    private final Map<World, Map<String, RegionZone>> regionZoneMap = new HashMap<>();
    private final Map<UUID, List<RegionZone>> entityRegions = new HashMap<>();
    private final Map<UUID, Pair<Location, Location>> playerZoneCornerSelection = new HashMap<>();
    private final Set<UUID> debugViewers = new HashSet<>();

    public RegionZoneManager() {}

    public void registerRegion(@NotNull World world, RegionZone regionZone) {
        regionZoneMap.computeIfAbsent(world, k -> new HashMap<>())
                .put(regionZone.getRegionName(), regionZone);
    }

    public void unregisterRegion(@NotNull World world, RegionZone regionZone) {
        Map<String, RegionZone> worldRegions = regionZoneMap.get(world);
        if (worldRegions != null) {
            worldRegions.remove(regionZone.getRegionName());
        }
    }

    public boolean isRegionZone(@NotNull Location location, RegionZone regionZone) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double xA = regionZone.getPosA().getX();
        double yA = regionZone.getPosA().getY();
        double zA = regionZone.getPosA().getZ();

        double xB = regionZone.getPosB().getX();
        double yB = regionZone.getPosB().getY();
        double zB = regionZone.getPosB().getZ();

        double minX = Math.min(xA, xB);
        double maxX = Math.max(xA, xB);
        double minY = Math.min(yA, yB);
        double maxY = Math.max(yA, yB);
        double minZ = Math.min(zA, zB);
        double maxZ = Math.max(zA,zB);

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    public List<RegionZone> getEntityCurrentRegions(@NotNull Entity entity) {
        final World world = entity.getWorld();
        List<RegionZone> entityRegions = new ArrayList<>();
        for (RegionZone zone : regionZoneMap.get(world).values()) {
            if (!isEntityInRegion(entity, zone)) continue;
            entityRegions.add(zone);
        }
        return entityRegions;
    }

    public void checkAndUpdateEntityRegionStatus(Entity entity) {
        UUID id = entity.getUniqueId();
        List<RegionZone> oldRegions = entityRegions.getOrDefault(id, new ArrayList<>());
        List<RegionZone> newRegions = getEntityCurrentRegions(entity);

        Iterator<RegionZone> it = oldRegions.iterator();
        while (it.hasNext()) {
            RegionZone zone = it.next();
            if (!newRegions.contains(zone)) {
                it.remove();
                Bukkit.getPluginManager().callEvent(new RegionZoneLeaveEvent(entity, zone));
            }
        }

        for (RegionZone zone : newRegions) {
            if (!oldRegions.contains(zone)) {
                oldRegions.add(zone);
                Bukkit.getPluginManager().callEvent(new RegionZoneJoinEvent(entity, zone));
            }
        }

        entityRegions.put(id, oldRegions);
    }

    public boolean isEntityInRegion(@NotNull Entity entity, RegionZone regionZone) {
        return isRegionZone(entity.getLocation(), regionZone);
    }

    public void select(Player player, Location locA, Location locB) {
        if (!playerZoneCornerSelection.containsKey(player.getUniqueId())) {
            playerZoneCornerSelection.put(player.getUniqueId(), Pair.of(locA, locB));
            return;
        }
        Pair<Location, Location> selection = playerZoneCornerSelection.get(player.getUniqueId());

        if (locA == null) {
            locA = selection.first();
        }
        if (locB == null) {
            locB = selection.second();
        }
        selection = Pair.of(locA, locB);
        playerZoneCornerSelection.put(player.getUniqueId(), selection);
    }

    public Pair<Location, Location> getSelection(Player player) {
        return playerZoneCornerSelection.get(player.getUniqueId());
    }
    public Set<UUID> getDebugViewers() {
        return Set.copyOf(debugViewers);
    }
    public void addDebugViewer(Player player) {
        if (debugViewers.contains(player.getUniqueId())) return;
        debugViewers.add(player.getUniqueId());
    }
    public void removeDebugViewer(Player player) {
        if (!debugViewers.contains(player.getUniqueId())) return;
        debugViewers.remove(player.getUniqueId());
    }
}
