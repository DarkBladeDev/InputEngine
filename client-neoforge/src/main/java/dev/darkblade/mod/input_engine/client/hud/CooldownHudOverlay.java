package dev.darkblade.mod.input_engine.client.hud;

import dev.darkblade.mod.input_engine.common.ClientConfig;
import dev.darkblade.mod.input_engine.common.CooldownManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import java.util.Map;

@EventBusSubscriber(modid = "input_engine", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class CooldownHudOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        CooldownManager.INSTANCE.tick();
        Map<String, CooldownManager.CooldownData> activeCooldowns = CooldownManager.INSTANCE.getActiveCooldowns();

        if (activeCooldowns.isEmpty()) return;

        int x = ClientConfig.INSTANCE.hudX;
        int y = ClientConfig.INSTANCE.hudY;

        long now = System.currentTimeMillis();
        GuiGraphics graphics = event.getGuiGraphics();

        for (Map.Entry<String, CooldownManager.CooldownData> entry : activeCooldowns.entrySet()) {
            String actionId = entry.getKey();
            CooldownManager.CooldownData data = entry.getValue();

            float progress = (float) (now - data.startTime) / data.durationMs;
            if (progress > 1.0f) progress = 1.0f;

            // Draw a simple background box
            graphics.fill(x, y, x + 20, y + 20, 0x80000000);
            
            // Draw progress bar
            int height = (int) (20 * progress);
            graphics.fill(x, y + 20 - height, x + 20, y + 20, 0x8000FF00);

            // Draw action name (first letter or so)
            String label = actionId.length() > 3 ? actionId.substring(0, 3) : actionId;
            graphics.drawString(client.font, label, x + 2, y + 6, 0xFFFFFF, false);

            x += 24; // offset for next icon
        }
    }
}
