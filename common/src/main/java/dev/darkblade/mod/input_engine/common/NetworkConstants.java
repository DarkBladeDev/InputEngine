package dev.darkblade.mod.input_engine.common;

public class NetworkConstants {
    public static final String CHANNEL_NAMESPACE = "inputengine";
    public static final String CHANNEL_PATH = "keystrokes";
    public static final String CONFIG_PATH = "config";
    public static final String COOLDOWN_PATH = "cooldown";
    public static final String BLOCK_INPUT_PATH = "block_input";
    
    public static final String FULL_CHANNEL = CHANNEL_NAMESPACE + ":" + CHANNEL_PATH;
    public static final String FULL_CONFIG_CHANNEL = CHANNEL_NAMESPACE + ":" + CONFIG_PATH;
    public static final String FULL_COOLDOWN_CHANNEL = CHANNEL_NAMESPACE + ":" + COOLDOWN_PATH;
    public static final String FULL_BLOCK_INPUT_CHANNEL = CHANNEL_NAMESPACE + ":" + BLOCK_INPUT_PATH;
}
