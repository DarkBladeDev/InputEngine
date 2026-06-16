package dev.darkblade.mod.input_engine.mixin;

import dev.darkblade.mod.input_engine.client.InputEngineClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onGetTranslation(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (InputEngineClient.DYNAMIC_TRANSLATIONS.containsKey(key)) {
            Map<String, String> translations = InputEngineClient.DYNAMIC_TRANSLATIONS.get(key);
            String currentLang = "en_us";
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getLanguageManager() != null) {
                currentLang = client.getLanguageManager().getLanguage();
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

    @Inject(method = "hasTranslation", at = @At("HEAD"), cancellable = true)
    private void onHasTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        if (InputEngineClient.DYNAMIC_TRANSLATIONS.containsKey(key)) {
            cir.setReturnValue(true);
        }
    }
}
