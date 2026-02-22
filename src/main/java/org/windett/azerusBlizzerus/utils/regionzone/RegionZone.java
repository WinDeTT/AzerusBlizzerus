package org.windett.azerusBlizzerus.utils.regionzone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.windett.azerusBlizzerus.Main;

import java.util.*;

public class RegionZone {

    private final @NotNull String regionName;
    private final String worldName;
    private final @NotNull Location posA;
    private final @NotNull Location posB;

    public enum RegionFlag {
        NOTICE_JOIN,
        NOTICE_QUIT,
        PVP
    }

    private final EnumMap<RegionFlag, Boolean> regionFlagMap = new EnumMap<>(RegionFlag.class);

    private final List<Location> kickLocations = new ArrayList<>();

    public RegionZone(@NotNull String regionName, String worldName, @NotNull Location posA, @NotNull Location posB) {
        this.regionName = regionName.toLowerCase();
        this.worldName = worldName;
        this.posA = posA;
        this.posB = posB;

        for (RegionFlag flag : RegionFlag.values()) {
            setFlag(flag, false);
        }
        setFlag(RegionFlag.PVP, true);

        Main.tweakManager.getRegionZoneManager().registerRegion(worldName, this);
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
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    public @NotNull Location getPosA() {
        return posA;
    }
    public @NotNull Location getPosB() {
        return posB;
    }
    public void addKickLocation(double x, double y, double z, float yaw, float pitch) {
        kickLocations.add(new Location(getWorld(), x,y,z,yaw,pitch));
    }
    public void addKickLocation(double x, double y, double z) {
        addKickLocation(x,y,z,0.0F,0.0F);
    }
    public List<Location> getKickLocations() {
        return kickLocations;
    }
}
