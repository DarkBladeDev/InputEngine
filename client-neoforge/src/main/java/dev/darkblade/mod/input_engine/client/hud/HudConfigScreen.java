package dev.darkblade.mod.input_engine.client.hud;

import dev.darkblade.mod.input_engine.common.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class HudConfigScreen extends Screen {

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudConfigScreen() {
        super(Component.literal("InputEngine HUD Config"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = ClientConfig.INSTANCE.hudX;
        int y = ClientConfig.INSTANCE.hudY;

        // Draw a dummy HUD element to show where it is
        graphics.fill(x, y, x + 68, y + 20, 0x80000000);
        graphics.drawString(this.font, "HUD Area", x + 5, y + 6, 0xFFFFFF, false);

        graphics.drawCenteredString(this.font, "Drag the HUD Area to move it. Press ESC to save.", this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = ClientConfig.INSTANCE.hudX;
        int y = ClientConfig.INSTANCE.hudY;

        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= x && mouseX <= x + 68 && mouseY >= y && mouseY <= y + 20) {
            isDragging = true;
            dragOffsetX = (int) (mouseX - x);
            dragOffsetY = (int) (mouseY - y);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && isDragging) {
            isDragging = false;
            ClientConfig.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            ClientConfig.INSTANCE.hudX = (int) (mouseX - dragOffsetX);
            ClientConfig.INSTANCE.hudY = (int) (mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
