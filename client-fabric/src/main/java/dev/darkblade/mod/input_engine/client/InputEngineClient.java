package dev.darkblade.mod.input_engine.client;

import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import dev.darkblade.mod.input_engine.common.KeyAction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class InputEngineClient implements ClientModInitializer {

    private final Map<KeyAction, KeyBinding> keyBindings = new HashMap<>();
    private final Map<KeyAction, Boolean> keyStates = new HashMap<>();

    @Override
    public void onInitializeClient() {
        registerKeys();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getNetworkHandler() == null) return;

            for (Map.Entry<KeyAction, KeyBinding> entry : keyBindings.entrySet()) {
                KeyAction action = entry.getKey();
                KeyBinding keyBinding = entry.getValue();
                
                boolean isCurrentlyPressed = keyBinding.isPressed();
                boolean wasPressed = keyStates.getOrDefault(action, false);

                if (isCurrentlyPressed != wasPressed) {
                    keyStates.put(action, isCurrentlyPressed);
                    // Send packet to server
                    ClientPlayNetworking.send(new KeystrokePayload(action.getId(), isCurrentlyPressed));
                }
            }
        });
    }

    private void registerKeys() {
        keyBindings.put(KeyAction.SKILL_1, KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.inputengine.skill1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.inputengine.keys"
        )));
        
        keyBindings.put(KeyAction.SKILL_2, KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.inputengine.skill2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.inputengine.keys"
        )));

        keyBindings.put(KeyAction.DODGE, KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.inputengine.dodge",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.inputengine.keys"
        )));

        // Default false state
        for (KeyAction action : keyBindings.keySet()) {
            keyStates.put(action, false);
        }
    }
}
