package org.windett.azerusBlizzerus.utils.pathRecorder;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.util.Vector;

public class PathTickData {

    public Vector getLocOffset() {
        return locOffset;
    }

    private final Vector locOffset;

    public Pair<Float, Float> getRotation() {
        return rotation;
    }

    private final Pair<Float, Float> rotation;

    public Pair<Boolean, Boolean> getSwingHands() {
        return swingHands;
    }
    public void setWasClicking(boolean leftHand, boolean rightHand) {
        this.swingHands = Pair.of(leftHand, rightHand);
    }

    private Pair<Boolean, Boolean> swingHands;

    public PathTickData(Vector locOffset, Pair<Float, Float> rotation, Pair<Boolean, Boolean> handSwinging) {
        this.locOffset = locOffset;
        this.rotation = rotation;
        this.swingHands = handSwinging;
    }
}
