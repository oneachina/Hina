/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.skia.EventSkiaDrawScene;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;
import com.eatgrapes.hina.ui.clickgui.Panel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final ClickGuiModule module;
    private final List<Panel> panels = new ArrayList<>();
    private float openAnimationProgress = 0f;
    private boolean closing = false;
    private final float GUI_SCALE = 1.2f;

    private int lastMouseX;
    private int lastMouseY;

    public ClickGuiScreen(ClickGuiModule module) {
        super(Component.translatable("hina.clickgui.name"));
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
    protected void init() {
        EventBus.INSTANCE.register(this);
    }

    @Override
    public void onClose() {
        EventBus.INSTANCE.unregister(this);
        super.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        assert this.minecraft != null;

        float target = closing ? 0f : 1f;
        openAnimationProgress += (target - openAnimationProgress) * 0.15f;

        if (closing && openAnimationProgress < 0.015f) {
            openAnimationProgress = 0f;
            this.minecraft.setScreen(null);
            return;
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();

        for (Panel panel : panels) {
            if (panel.handleKeyPress(keyCode)) return true;
        }

        if (keyEvent.isEscape() || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean canHandle) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();

        double mcScale = this.minecraft.getWindow().getGuiScale();
        double transformedMouseX = mouseX * (mcScale / GUI_SCALE);
        double transformedMouseY = mouseY * (mcScale / GUI_SCALE);

        for (Panel panel : panels) {
            if (panel.mouseClicked(transformedMouseX, transformedMouseY, button)) return true;
        }

        return super.mouseClicked(mouseButtonEvent, canHandle);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();

        double mcScale = this.minecraft.getWindow().getGuiScale();
        double transformedMouseX = mouseX * (mcScale / GUI_SCALE);
        double transformedMouseY = mouseY * (mcScale / GUI_SCALE);

        for (Panel panel : panels) {
            panel.mouseReleased(transformedMouseX, transformedMouseY, button);
        }

        return super.mouseReleased(mouseButtonEvent);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @EventListener
    public void onSkiaRender(EventSkiaDrawScene event) {
        if (this.minecraft.screen != this) {
            EventBus.INSTANCE.unregister(this);
            return;
        }

        double mcScale = this.minecraft.getWindow().getGuiScale();
        var canvas = event.getCanvas();

        int transformedMouseX = (int) (lastMouseX * (mcScale / GUI_SCALE));
        int transformedMouseY = (int) (lastMouseY * (mcScale / GUI_SCALE));

        if (closing && openAnimationProgress < 0.02f) return;
        if (openAnimationProgress < 0.01f) return;

        canvas.save();
        canvas.scale(GUI_SCALE, GUI_SCALE);

        float logicalWidth = this.minecraft.getWindow().getHeight() / GUI_SCALE;
        float logicalHeight = this.minecraft.getWindow().getWidth() / GUI_SCALE;
        float centerX = logicalWidth / 2f;
        float centerY = logicalHeight / 2f;

        canvas.translate(centerX, centerY);
        canvas.scale(openAnimationProgress, openAnimationProgress);
        canvas.translate(-centerX, -centerY);

        for (Panel panel : panels) panel.render(canvas, transformedMouseX, transformedMouseY);

        canvas.restore();
    }
}