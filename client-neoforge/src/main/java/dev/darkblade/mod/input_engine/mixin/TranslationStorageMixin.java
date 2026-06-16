package dev.darkblade.mod.input_engine.mixin;

import dev.darkblade.mod.input_engine.client.InputEngineNeoForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ClientLanguage.class)
public class TranslationStorageMixin {

    @Inject(method = "getOrDefault", at = @At("HEAD"), cancellable = true)
    private void onGetTranslation(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (InputEngineNeoForge.DYNAMIC_TRANSLATIONS.containsKey(key)) {
            Map<String, String> translations = InputEngineNeoForge.DYNAMIC_TRANSLATIONS.get(key);
            String currentLang = "en_us";
            Minecraft client = Minecraft.getInstance();
            if (client != null && client.getLanguageManager() != null) {
                currentLang = client.getLanguageManager().getSelected();
            }

            if (translations.containsKey(currentLang)) {
                cir.setReturnValue(translations.get(currentLang));
            } else if (translations.containsKey("en_us")) {
                cir.setReturnValue(translations.get("en_us"));
            } else if (!translations.isEmpty()) {
                cir.setReturnValue(translations.values().iterator().next());
            }
        }
    }

    @Inject(method = "has", at = @At("HEAD"), cancellable = true)
    private void onHasTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        if (InputEngineNeoForge.DYNAMIC_TRANSLATIONS.containsKey(key)) {
            cir.setReturnValue(true);
        }
    }
}
