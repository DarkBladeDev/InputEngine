package dev.darkblade.mod.input_engine.mixin;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.darkblade.mod.input_engine.client.InputEngineClient;
import dev.darkblade.mod.input_engine.common.ServerKeybindStore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import java.util.Map;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "write", at = @At("TAIL"))
    private void onWrite(CallbackInfo ci) {
        String serverIp = "singleplayer";
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            serverIp = MinecraftClient.getInstance().getCurrentServerEntry().address;
        }

        boolean hasChanges = false;
        for (Map.Entry<String, KeyBinding> entry : InputEngineClient.dynamicKeyBindings.entrySet()) {
            String actionId = entry.getKey();
            KeyBinding binding = entry.getValue();
            String currentTranslation = binding.getBoundKeyTranslationKey();
            
            String saved = ServerKeybindStore.getSavedKey(serverIp, actionId);
            if (!currentTranslation.equals(saved)) {
                ServerKeybindStore.saveKey(serverIp, actionId, currentTranslation);
                hasChanges = true;
            }
        }
        
        if (hasChanges) {
            ServerKeybindStore.save();
        }
    }
}
