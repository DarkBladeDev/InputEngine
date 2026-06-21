package dev.darkblade.mod.input_engine.client;

import dev.darkblade.mod.input_engine.client.network.KeybindConfigPayload;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import dev.darkblade.mod.input_engine.client.network.InputBlockPayload;
import dev.darkblade.mod.input_engine.client.network.CooldownPayload;
import dev.darkblade.mod.input_engine.common.KeyState;
import dev.darkblade.mod.input_engine.mixin.OptionsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
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
    public static final Map<String, KeyState> keyStates = new HashMap<>();
    public static final Map<String, Map<String, String>> DYNAMIC_TRANSLATIONS = new HashMap<>();

    public InputEngineNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
    }

    private boolean isConflicting(Minecraft client, KeyMapping binding) {
        for (KeyMapping other : client.options.keyMappings) {
            if (other != binding && !dynamicKeyBindings.containsValue(other)) {
                if (!other.isUnbound() && other.same(binding)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        dev.darkblade.mod.input_engine.common.ClientConfig.load(net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().toFile());
        CategoryFixer.fix();
    }

    private void onRegisterClientCommands(net.neoforged.neoforge.client.event.RegisterClientCommandsEvent event) {
        event.getDispatcher().register(net.minecraft.commands.Commands.literal("inputengine")
            .then(net.minecraft.commands.Commands.literal("hud")
                .executes(context -> {
                    Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new dev.darkblade.mod.input_engine.client.hud.HudConfigScreen());
                    });
                    return 1;
                })));
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
                        
                        KeyState state = new KeyState();
                        state.updateConfig(data.requiresDoubleTap(), data.trackHoldDuration(), data.hasShift(), data.hasCtrl(), data.hasAlt());
                        keyStates.put(data.actionId(), state);

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

        registrar.playToClient(InputBlockPayload.TYPE, InputBlockPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                KeyState state = keyStates.get(payload.actionId());
                if (state != null) {
                    state.isBlocked = payload.isBlocked();
                }
            });
        });
        
        registrar.playToClient(CooldownPayload.TYPE, CooldownPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                dev.darkblade.mod.input_engine.common.CooldownManager.INSTANCE.addCooldown(payload.actionId(), payload.durationMs());
            });
        });
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.getConnection() == null) return;

        long currentTime = System.currentTimeMillis();
        long window = client.getWindow().getWindow();

        for (Map.Entry<String, KeyMapping> entry : dynamicKeyBindings.entrySet()) {
            String actionId = entry.getKey();
            KeyMapping keyBinding = entry.getValue();
            KeyState state = keyStates.get(actionId);

            if (state == null || state.isBlocked) continue;

            boolean rawPressed = keyBinding.isDown();

            // Modifiers check
            if (rawPressed) {
                if (isConflicting(client, keyBinding)) {
                    rawPressed = false;
                } else {
                    if (state.hasShift && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT)) rawPressed = false;
                    if (state.hasCtrl && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL) && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL)) rawPressed = false;
                    if (state.hasAlt && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT) && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT)) rawPressed = false;
                }
            }

            boolean isCurrentlyPressed = rawPressed;
            boolean wasPressed = state.isPressed;

            if (isCurrentlyPressed && !wasPressed) {
                state.isPressed = true;
                state.pressStartTime = currentTime;
                
                if (state.requiresDoubleTap) {
                    if (currentTime - state.lastReleaseTime <= 300) {
                        var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
                        if (conn != null) conn.send(new KeystrokePayload(actionId, true, 0, true));
                        state.lastReleaseTime = 0;
                    }
                } else {
                    var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
                    if (conn != null) conn.send(new KeystrokePayload(actionId, true, 0, false));
                }
            } else if (!isCurrentlyPressed && wasPressed) {
                state.isPressed = false;
                long holdDuration = currentTime - state.pressStartTime;
                state.lastReleaseTime = currentTime;
                
                if (state.trackHoldDuration && holdDuration > 0) {
                    var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
                    if (conn != null) conn.send(new KeystrokePayload(actionId, false, holdDuration, false));
                } else if (!state.requiresDoubleTap) {
                    var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
                    if (conn != null) conn.send(new KeystrokePayload(actionId, false, 0, false));
                }
            }
        }
    }
}
