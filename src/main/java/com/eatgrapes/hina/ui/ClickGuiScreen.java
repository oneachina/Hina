/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui;

import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;
import com.eatgrapes.hina.skia.SkiaRenderer;
import com.eatgrapes.hina.ui.clickgui.Panel;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final ClickGuiModule module;
    private final List<Panel> panels = new ArrayList<>();
    private float openAnimationProgress = 0f;
    private boolean closing = false;
    private final float GUI_SCALE = 1.2f;

    public ClickGuiScreen(ClickGuiModule module) {
        super(Text.of("ClickGUI"));
        this.module = module;
        float x = 20;
        float y = 20;
        float width = 140; 
        float gap = 20; 
        for (Category category : Category.values()) {
            panels.add(new Panel(category, x, y, width));
            x += width + gap + 30; 
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float target = closing ? 0.0f : 1.0f;
        openAnimationProgress += (target - openAnimationProgress) * 0.15f;
        if (closing && openAnimationProgress < 0.05f) {
            this.client.setScreen(null);
            this.module.setEnabled(false);
            return;
        }
        double mcScale = this.client.getWindow().getScaleFactor();
        int transformedMouseX = (int) (mouseX * (mcScale / GUI_SCALE));
        int transformedMouseY = (int) (mouseY * (mcScale / GUI_SCALE));
        SkiaRenderer.draw((canvas) -> {
            if (openAnimationProgress < 0.01f) return;
            canvas.save();
            canvas.scale(GUI_SCALE, GUI_SCALE);
            float logicalWidth = this.client.getWindow().getFramebufferWidth() / GUI_SCALE;
            float logicalHeight = this.client.getWindow().getFramebufferHeight() / GUI_SCALE;
            float centerX = logicalWidth / 2f;
            float centerY = logicalHeight / 2f;
            canvas.translate(centerX, centerY);
            canvas.scale(openAnimationProgress, openAnimationProgress);
            canvas.translate(-centerX, -centerY);
            for (Panel panel : panels) panel.render(canvas, transformedMouseX, transformedMouseY);
            canvas.restore();
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mcScale = this.client.getWindow().getScaleFactor();
        double transformedMouseX = mouseX * (mcScale / GUI_SCALE);
        double transformedMouseY = mouseY * (mcScale / GUI_SCALE);
        for (Panel panel : panels) {
            if (panel.mouseClicked(transformedMouseX, transformedMouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double mcScale = this.client.getWindow().getScaleFactor();
        double transformedMouseX = mouseX * (mcScale / GUI_SCALE);
        double transformedMouseY = mouseY * (mcScale / GUI_SCALE);
        for (Panel panel : panels) panel.mouseReleased(transformedMouseX, transformedMouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            closing = true;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}