package org.windett.azerusBlizzerus.utils.regionzone;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;

import java.util.*;

public class RegionZone {

    private final @NotNull String regionName;
    private final @NotNull World world;
    private final @NotNull Location posA;
    private final @NotNull Location posB;

    public enum RegionFlag {
        NOTICE_JOIN,
        NOTICE_QUIT,
        PVP
    }

    private final EnumMap<RegionFlag, Boolean> regionFlagMap = new EnumMap<>(RegionFlag.class);

    private final List<Location> kickLocations = new ArrayList<>();

    public RegionZone(@NotNull String regionName, @NotNull  World world, @NotNull Location posA, @NotNull Location posB) {
        this.regionName = regionName.toLowerCase();
        this.world = world;
        this.posA = posA;
        this.posB = posB;

        for (RegionFlag flag : RegionFlag.values()) {
            setFlag(flag, false);
        }
        setFlag(RegionFlag.PVP, true);

        Main.tweakManager.getRegionZoneManager().registerRegion(world, this);
    }

    public void setFlag(RegionFlag flag, boolean b) {
        regionFlagMap.put(flag, b);
    }

    public Object hasFlag(RegionFlag flag) {
        return regionFlagMap.get(flag);
    }
    public @NotNull String getRegionName() {
        return regionName;
    }
    public @NotNull World getWorld() {
        return world;
    }
    public @NotNull Location getPosA() {
        return posA;
    }
    public @NotNull Location getPosB() {
        return posB;
    }
    public void addKickLocation(double x, double y, double z, float yaw, float pitch) {
        kickLocations.add(new Location(world, x,y,z,yaw,pitch));
    }
    public void addKickLocation(double x, double y, double z) {
        addKickLocation(x,y,z,0.0F,0.0F);
    }
    public List<Location> getKickLocations() {
        return kickLocations;
    }
}
