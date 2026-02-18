/*
 * Hina Client
 * Copyright (C) 2026 Hina Client
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.hinaclient.hina.ui;

import com.hinaclient.hina.event.EventBus;
import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.skia.EventSkiaDrawScene;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.ui.clickgui.Panel;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
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
    private final List<Panel> panels = new ArrayList<>();
    private float openAnimationProgress = 0f;
    private final boolean closing = false;
    private final float GUI_SCALE = 1.2f;

    private int lastMouseX;
    private int lastMouseY;
    private float editButtonHover = 0f;
    private final float EDIT_BUTTON_WIDTH = 100;
    private final float EDIT_BUTTON_HEIGHT = 35;
    private final float EDIT_BUTTON_X = 20;
    private float EDIT_BUTTON_Y;

    public ClickGuiScreen() {
        super(Component.translatable("hina.clickgui.name"));
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

        if (transformedMouseX >= EDIT_BUTTON_X && transformedMouseX <= EDIT_BUTTON_X + EDIT_BUTTON_WIDTH && 
            transformedMouseY >= EDIT_BUTTON_Y - EDIT_BUTTON_HEIGHT && transformedMouseY <= EDIT_BUTTON_Y) {
            this.minecraft.setScreen(new EditScreen());
            return true;
        }

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

        EDIT_BUTTON_Y = logicalWidth - EDIT_BUTTON_HEIGHT - 20;

        boolean isHoveringEditButton = transformedMouseX >= EDIT_BUTTON_X && transformedMouseX <= EDIT_BUTTON_X + EDIT_BUTTON_WIDTH && 
                                      transformedMouseY >= EDIT_BUTTON_Y - EDIT_BUTTON_HEIGHT && transformedMouseY <= EDIT_BUTTON_Y;
        editButtonHover = isHoveringEditButton ? Math.min(1f, editButtonHover + 0.15f) : Math.max(0f, editButtonHover - 0.15f);

        for (Panel panel : panels) panel.render(canvas, transformedMouseX, transformedMouseY);

        try (Paint paint = new Paint()) {
            int COLOR_GEHENNA_BLUE = 0xFF5C6BC0;
            paint.setColor(COLOR_GEHENNA_BLUE);
            canvas.drawRRect(RRect.makeXYWH(EDIT_BUTTON_X, EDIT_BUTTON_Y - EDIT_BUTTON_HEIGHT, EDIT_BUTTON_WIDTH, EDIT_BUTTON_HEIGHT, 8), paint);

            if (editButtonHover > 0) {
                int COLOR_HINA_PURPLE = 0xFFD1C4E9;
                paint.setMode(PaintMode.STROKE).setStrokeWidth(2f * editButtonHover).setColor(COLOR_HINA_PURPLE);
                canvas.drawRRect(RRect.makeXYWH(EDIT_BUTTON_X, EDIT_BUTTON_Y - EDIT_BUTTON_HEIGHT, EDIT_BUTTON_WIDTH, EDIT_BUTTON_HEIGHT, 8), paint);
            }

            paint.setMode(PaintMode.FILL).setColor(0xFFFFFFFF);
            canvas.drawString("Edit", EDIT_BUTTON_X + 40, EDIT_BUTTON_Y - 10, FontManager.INSTANCE.getTextFont(14f), paint);
        }

        canvas.restore();
    }
}