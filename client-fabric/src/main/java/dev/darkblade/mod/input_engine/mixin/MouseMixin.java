package dev.darkblade.mod.input_engine.mixin;

import dev.darkblade.mod.input_engine.client.InputEngineClient;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import dev.darkblade.mod.input_engine.common.KeyState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        String actionId = "mouse.button." + button;
        boolean isPressed = action == 1;
        
        // This is a naive implementation; ideally we'd map this to a dynamic binding.
        // If a server registered a combo expecting a mouse click, we can send it.
        // We'll let InputEngineClient tick handle actual bound keys, 
        // but for pure mouse clicks not mapped to a KeyBinding, we can send raw events if needed.
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (vertical != 0 && net.minecraft.client.MinecraftClient.getInstance().player != null) {
            String scrollAction = vertical > 0 ? "mouse.scroll.up" : "mouse.scroll.down";
            if (ClientPlayNetworking.canSend(KeystrokePayload.ID)) {
                // We could send a KeystrokePayload for the scroll
                ClientPlayNetworking.send(new KeystrokePayload(scrollAction, true, 0, false));
                // Send immediate release for scroll
                ClientPlayNetworking.send(new KeystrokePayload(scrollAction, false, 0, false));
            }
        }
    }
}
