package dev.darkblade.mod.input_engine.mixin;

import dev.darkblade.mod.input_engine.client.InputEngineNeoForge;
import dev.darkblade.mod.input_engine.client.network.KeystrokePayload;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"))
    private void onPress(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        String actionId = "mouse.button." + button;
        boolean isPressed = action == 1;
        // Naive implementation, similar to Fabric. 
        // Can be extended if server specifically expects a "mouse.button.X" action.
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (yOffset != 0 && net.minecraft.client.Minecraft.getInstance().player != null) {
            String scrollAction = yOffset > 0 ? "mouse.scroll.up" : "mouse.scroll.down";
            var conn = net.minecraft.client.Minecraft.getInstance().getConnection();
            if (conn != null && conn.hasChannel(KeystrokePayload.TYPE)) {
                conn.send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(new KeystrokePayload(scrollAction, true, 0, false)));
                conn.send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(new KeystrokePayload(scrollAction, false, 0, false)));
            }
        }
    }
}
