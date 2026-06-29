package dev.darkblade.mod.input_engine.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.darkblade.mod.input_engine.client.InputEngineNeoForge;
import dev.darkblade.mod.input_engine.common.ServerKeybindStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import java.util.Map;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "save", at = @At("TAIL"))
    private void onSave(CallbackInfo ci) {
        String serverIp = "singleplayer";
        if (Minecraft.getInstance().getCurrentServer() != null) {
            serverIp = Minecraft.getInstance().getCurrentServer().ip;
        }

        boolean hasChanges = false;
        for (Map.Entry<String, KeyMapping> entry : InputEngineNeoForge.dynamicKeyBindings.entrySet()) {
            String actionId = entry.getKey();
            KeyMapping binding = entry.getValue();
            String currentTranslation = binding.saveString();
            
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
