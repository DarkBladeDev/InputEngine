package dev.darkblade.mod.input_engine.client;

import dev.darkblade.mod.input_engine.client.network.KeybindConfigPayload;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import dev.darkblade.mod.input_engine.client.network.InputBlockPayload;
import dev.darkblade.mod.input_engine.common.KeyState;

public class InputEngineClient implements ClientModInitializer {

    private final Map<String, KeyBinding> dynamicKeyBindings = new HashMap<>();
    public static final Map<String, KeyState> keyStates = new HashMap<>();
    public static final Map<String, Map<String, String>> DYNAMIC_TRANSLATIONS = new HashMap<>();

    private boolean isConflicting(MinecraftClient client, KeyBinding binding) {
        for (KeyBinding other : client.options.allKeys) {
            if (other != binding && !dynamicKeyBindings.containsValue(other)) {
                if (!other.isUnbound() && other.getBoundKeyTranslationKey().equals(binding.getBoundKeyTranslationKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInitializeClient() {
        dev.darkblade.mod.input_engine.common.ClientConfig.load(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile());
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register(new dev.darkblade.mod.input_engine.client.hud.CooldownHudOverlay());
        CategoryFixer.fix();
        ClientPlayNetworking.registerGlobalReceiver(KeybindConfigPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // Register dynamically received keys
                for (KeybindConfigPayload.KeybindData data : payload.keys()) {
                    if (!dynamicKeyBindings.containsKey(data.actionId())) {
                        KeyBinding keyBinding = new KeyBinding(
                                data.translationKey(),
                                InputUtil.Type.KEYSYM,
                                data.defaultKey(),
                                "category.inputengine.keys"
                        );
                        dynamicKeyBindings.put(data.actionId(), keyBinding);
                        
                        KeyState state = new KeyState();
                        state.updateConfig(data.requiresDoubleTap(), data.trackHoldDuration(), data.hasShift(), data.hasCtrl(), data.hasAlt());
                        keyStates.put(data.actionId(), state);
                        
                        if (data.translations() != null && !data.translations().isEmpty()) {
                            DYNAMIC_TRANSLATIONS.put(data.translationKey(), data.translations());
                        }
                        
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client != null && client.options != null) {
                            dev.darkblade.mod.input_engine.mixin.GameOptionsAccessor accessor = 
                                (dev.darkblade.mod.input_engine.mixin.GameOptionsAccessor) client.options;
                            KeyBinding[] oldKeys = accessor.getAllKeys();
                            boolean exists = false;
                            for (KeyBinding oldKey : oldKeys) {
                                if (oldKey == keyBinding) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                KeyBinding[] newKeys = new KeyBinding[oldKeys.length + 1];
                                System.arraycopy(oldKeys, 0, newKeys, 0, oldKeys.length);
                                newKeys[oldKeys.length] = keyBinding;
                                accessor.setAllKeys(newKeys);
                                KeyBinding.updateKeysByCode();
                            }
                        }
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(dev.darkblade.mod.input_engine.client.network.CooldownPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                dev.darkblade.mod.input_engine.common.CooldownManager.INSTANCE.addCooldown(payload.actionId(), payload.durationMs());
            });
        });

        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("inputengine")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("hud")
                    .executes(context -> {
                        MinecraftClient.getInstance().send(() -> {
                            MinecraftClient.getInstance().setScreen(new dev.darkblade.mod.input_engine.client.hud.HudConfigScreen());
                        });
                        return 1;
                    })));
        });

        ClientPlayNetworking.registerGlobalReceiver(InputBlockPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                KeyState state = keyStates.get(payload.actionId());
                if (state != null) {
                    state.isBlocked = payload.isBlocked();
                }
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getNetworkHandler() == null) return;

            long currentTime = System.currentTimeMillis();
            long window = client.getWindow().getHandle();

            for (Map.Entry<String, KeyBinding> entry : dynamicKeyBindings.entrySet()) {
                String actionId = entry.getKey();
                KeyBinding keyBinding = entry.getValue();
                KeyState state = keyStates.get(actionId);
                
                if (state == null || state.isBlocked) continue;

                boolean rawPressed = keyBinding.isPressed();
                
                // Modifiers check
                if (rawPressed) {
                    if (isConflicting(client, keyBinding)) {
                        rawPressed = false;
                    } else {
                        if (state.hasShift && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT)) rawPressed = false;
                        if (state.hasCtrl && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL)) rawPressed = false;
                        if (state.hasAlt && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) && !InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT)) rawPressed = false;
                    }
                }

                boolean isCurrentlyPressed = rawPressed;
                boolean wasPressed = state.isPressed;

                if (isCurrentlyPressed && !wasPressed) {
                    // Just pressed
                    state.isPressed = true;
                    state.pressStartTime = currentTime;
                    
                    if (state.requiresDoubleTap) {
                        if (currentTime - state.lastReleaseTime <= 300) {
                            // Valid double tap
                            if (ClientPlayNetworking.canSend(KeystrokePayload.ID)) ClientPlayNetworking.send(new KeystrokePayload(actionId, true, 0, true));
                            state.lastReleaseTime = 0; // consume double tap
                        }
                    } else {
                        // Normal press
                        if (ClientPlayNetworking.canSend(KeystrokePayload.ID)) ClientPlayNetworking.send(new KeystrokePayload(actionId, true, 0, false));
                    }
                } else if (!isCurrentlyPressed && wasPressed) {
                    // Just released
                    state.isPressed = false;
                    long holdDuration = currentTime - state.pressStartTime;
                    state.lastReleaseTime = currentTime;
                    
                    if (state.trackHoldDuration && holdDuration > 0) {
                        if (ClientPlayNetworking.canSend(KeystrokePayload.ID)) ClientPlayNetworking.send(new KeystrokePayload(actionId, false, holdDuration, false));
                    } else if (!state.requiresDoubleTap) {
                        // Normal release
                        if (ClientPlayNetworking.canSend(KeystrokePayload.ID)) ClientPlayNetworking.send(new KeystrokePayload(actionId, false, 0, false));
                    }
                }
            }
        });
    }
}
