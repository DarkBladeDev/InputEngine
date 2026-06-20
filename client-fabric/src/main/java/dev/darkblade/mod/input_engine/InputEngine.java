package dev.darkblade.mod.input_engine;

import dev.darkblade.mod.input_engine.client.network.KeybindConfigPayload;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import dev.darkblade.mod.input_engine.client.network.CooldownPayload;
import dev.darkblade.mod.input_engine.client.network.InputBlockPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputEngine implements ModInitializer {
	public static final String MOD_ID = "input-engine";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(KeystrokePayload.ID, KeystrokePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(KeybindConfigPayload.ID, KeybindConfigPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CooldownPayload.ID, CooldownPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(InputBlockPayload.ID, InputBlockPayload.CODEC);
	}
}