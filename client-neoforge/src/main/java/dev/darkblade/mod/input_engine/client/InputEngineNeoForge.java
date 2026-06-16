package dev.darkblade.mod.input_engine.client;

import dev.darkblade.mod.input_engine.client.network.KeybindConfigPayload;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import dev.darkblade.mod.input_engine.mixin.OptionsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Mod("input_engine")
public class InputEngineNeoForge {
    public static final String MOD_ID = "input_engine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final Map<String, KeyMapping> dynamicKeyBindings = new HashMap<>();
    private final Map<String, Boolean> keyStates = new HashMap<>();
    public static final Map<String, Map<String, String>> DYNAMIC_TRANSLATIONS = new HashMap<>();

    public InputEngineNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        CategoryFixer.fix();
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID).optional();

        registrar.playToServer(KeystrokePayload.TYPE, KeystrokePayload.CODEC, (payload, context) -> {
            // Not handled on client
        });

        registrar.playToClient(KeybindConfigPayload.TYPE, KeybindConfigPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                for (KeybindConfigPayload.KeybindData data : payload.keys()) {
                    if (!dynamicKeyBindings.containsKey(data.actionId())) {
                        KeyMapping keyBinding = new KeyMapping(
                                data.translationKey(),
                                InputConstants.Type.KEYSYM,
                                data.defaultKey(),
                                "category.inputengine.keys"
                        );
                        dynamicKeyBindings.put(data.actionId(), keyBinding);
                        keyStates.put(data.actionId(), false);

                        if (data.translations() != null && !data.translations().isEmpty()) {
                            DYNAMIC_TRANSLATIONS.put(data.translationKey(), data.translations());
                        }

                        Minecraft client = Minecraft.getInstance();
                        if (client != null && client.options != null) {
                            OptionsAccessor accessor = (OptionsAccessor) client.options;
                            KeyMapping[] oldKeys = accessor.getAllKeys();
                            boolean exists = false;
                            for (KeyMapping oldKey : oldKeys) {
                                if (oldKey == keyBinding) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                KeyMapping[] newKeys = new KeyMapping[oldKeys.length + 1];
                                System.arraycopy(oldKeys, 0, newKeys, 0, oldKeys.length);
                                newKeys[oldKeys.length] = keyBinding;
                                accessor.setAllKeys(newKeys);
                                KeyMapping.resetMapping();
                            }
                        }
                    }
                }
            });
        });
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.getConnection() == null) return;

        for (Map.Entry<String, KeyMapping> entry : dynamicKeyBindings.entrySet()) {
            String actionId = entry.getKey();
            KeyMapping keyBinding = entry.getValue();

            boolean isCurrentlyPressed = keyBinding.isDown();
            boolean wasPressed = keyStates.getOrDefault(actionId, false);

            if (isCurrentlyPressed != wasPressed) {
                keyStates.put(actionId, isCurrentlyPressed);
                                var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
                if (conn != null) {
                    conn.send(new KeystrokePayload(actionId, isCurrentlyPressed));
                }
            }
        }
    }
}
