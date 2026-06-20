package dev.darkblade.mod.input_engine.client.hud;

import dev.darkblade.mod.input_engine.common.ClientConfig;
import dev.darkblade.mod.input_engine.common.CooldownManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import java.util.Map;

public class CooldownHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        CooldownManager.INSTANCE.tick();
        Map<String, CooldownManager.CooldownData> activeCooldowns = CooldownManager.INSTANCE.getActiveCooldowns();

        if (activeCooldowns.isEmpty()) return;

        int x = ClientConfig.INSTANCE.hudX;
        int y = ClientConfig.INSTANCE.hudY;

        long now = System.currentTimeMillis();

        for (Map.Entry<String, CooldownManager.CooldownData> entry : activeCooldowns.entrySet()) {
            String actionId = entry.getKey();
            CooldownManager.CooldownData data = entry.getValue();

            float progress = (float) (now - data.startTime) / data.durationMs;
            if (progress > 1.0f) progress = 1.0f;

            // Draw a simple background box
            drawContext.fill(x, y, x + 20, y + 20, 0x80000000);
            
            // Draw progress bar
            int height = (int) (20 * progress);
            drawContext.fill(x, y + 20 - height, x + 20, y + 20, 0x8000FF00);

            // Draw action name (first letter or so)
            String label = actionId.length() > 3 ? actionId.substring(0, 3) : actionId;
            drawContext.drawText(client.textRenderer, label, x + 2, y + 6, 0xFFFFFF, false);

            x += 24; // offset for next icon
        }
    }
}
