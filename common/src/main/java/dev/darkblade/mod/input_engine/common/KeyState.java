package dev.darkblade.mod.input_engine.common;

public class KeyState {
    public boolean isPressed = false;
    public long pressStartTime = 0;
    public long lastReleaseTime = 0;
    
    // For combos/modifiers we might need config
    public boolean requiresDoubleTap = false;
    public boolean trackHoldDuration = false;
    public boolean hasShift = false;
    public boolean hasCtrl = false;
    public boolean hasAlt = false;
    public boolean isBlocked = false;

    public void updateConfig(boolean requiresDoubleTap, boolean trackHoldDuration, boolean hasShift, boolean hasCtrl, boolean hasAlt) {
        this.requiresDoubleTap = requiresDoubleTap;
        this.trackHoldDuration = trackHoldDuration;
        this.hasShift = hasShift;
        this.hasCtrl = hasCtrl;
        this.hasAlt = hasAlt;
    }
}
