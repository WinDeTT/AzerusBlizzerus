package org.windett.azerusBlizzerus.utils.pathRecorder;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.util.Vector;

public class PathPositionData {

    public Vector getLocOffset() {
        return locOffset;
    }

    private final Vector locOffset;

    public Pair<Float, Float> getRotation() {
        return rotation;
    }

    private final Pair<Float, Float> rotation;

    public PathPositionData(Vector locOffset, Pair<Float, Float> rotation) {
        this.locOffset = locOffset;
        this.rotation = rotation;
    }
}
