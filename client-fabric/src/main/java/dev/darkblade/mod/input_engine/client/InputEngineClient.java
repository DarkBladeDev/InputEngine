package dev.darkblade.mod.input_engine.client;

import dev.darkblade.mod.input_engine.client.network.KeybindConfigPayload;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;

public class InputEngineClient implements ClientModInitializer {

    private final Map<String, KeyBinding> dynamicKeyBindings = new HashMap<>();
    private final Map<String, Boolean> keyStates = new HashMap<>();

    @Override
    public void onInitializeClient() {
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
                        keyStates.put(data.actionId(), false);
                        
                        // Force update Minecraft options to show new keys in menu if already initialized
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getNetworkHandler() == null) return;

            for (Map.Entry<String, KeyBinding> entry : dynamicKeyBindings.entrySet()) {
                String actionId = entry.getKey();
                KeyBinding keyBinding = entry.getValue();
                
                boolean isCurrentlyPressed = keyBinding.isPressed();
                boolean wasPressed = keyStates.getOrDefault(actionId, false);

                if (isCurrentlyPressed != wasPressed) {
                    keyStates.put(actionId, isCurrentlyPressed);
                    // Send packet to server
                    ClientPlayNetworking.send(new KeystrokePayload(actionId, isCurrentlyPressed));
                }
            }
        });
    }
}
