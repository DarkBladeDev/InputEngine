package dev.darkblade.mod.input_engine;

import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
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
	}
}