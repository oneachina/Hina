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

package com.hinaclient.hina.ui.clickgui;

import com.hinaclient.hina.HinaClient;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.skia.SkiaRenderer;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.skia.font.Icon;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public class Panel {
    private final Category category;
    private float x, y;
    private final float width;
    private float height;
    private boolean expanded;
    private boolean dragging;
    private float dragX, dragY;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private float animationProgress;
    private float arrowRotation;
    private float hoverAlpha = 0f;
    private Shader currentShader = null;

    private static final float HEADER_HEIGHT = 38;
    private static final float CORNER_RADIUS = 14;
    private static final float FONT_SIZE_HEADER = 16;
    private static final float ICON_SIZE = 18;
    private static final float MODULE_BUTTON_HEIGHT = 32;
    private static final int GLASS_BG = 0xC01A1A2E;
    private static final int GLASS_BORDER = 0x33FFFFFF;
    private static final int TEXT_PRIMARY = 0xFFFFFFFF;
    private static final int TEXT_SECONDARY = 0xCCFFFFFF;

    public Panel(Category category, float x, float y, float width) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width + 30;
        this.height = HEADER_HEIGHT;
        this.expanded = true;
        this.animationProgress = 1.0f;
        for (Module module : HinaClient.getINSTANCE().moduleManager.getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(module, this.width, MODULE_BUTTON_HEIGHT));
        }
    }

    public void render(Canvas canvas, int mouseX, int mouseY, Shader glassShader) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
            clampToScreen();
        }

        float targetProgress = expanded ? 1.0f : 0.0f;
        animationProgress += (targetProgress - animationProgress) * 0.2f;
        if (Math.abs(targetProgress - animationProgress) < 0.001f) animationProgress = targetProgress;

        float targetRotation = expanded ? 180f : 0f;
        arrowRotation += (targetRotation - arrowRotation) * 0.2f;
        if (Math.abs(targetRotation - arrowRotation) < 0.5f) arrowRotation = targetRotation;

        for (ModuleButton btn : moduleButtons) btn.update();

        float modulesHeight = 0;
        if (animationProgress > 0.01f) {
            for (ModuleButton btn : moduleButtons) modulesHeight += btn.getTotalHeight();
        }
        float currentContentHeight = modulesHeight * animationProgress;
        float totalHeight = HEADER_HEIGHT + currentContentHeight;

        try (Paint shadow = new Paint()) {
            shadow.setColor(0x40000000);
            shadow.setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, 12));
            canvas.drawRRect(RRect.makeXYWH(x + 2, y + 4, width, totalHeight, CORNER_RADIUS), shadow);
        }

        if (glassShader != null) {
            try (Paint glassPaint = new Paint().setShader(glassShader)) {
                canvas.drawRRect(RRect.makeXYWH(x, y, width, height, CORNER_RADIUS), glassPaint);
            }
        } else {
            try (Paint fallback = new Paint().setColor(GLASS_BG)) {
                canvas.drawRRect(RRect.makeXYWH(x, y, width, height, CORNER_RADIUS), fallback);
            }
        }

        try (Paint border = new Paint()) {
            border.setMode(PaintMode.STROKE);
            border.setStrokeWidth(1.5f);
            border.setColor(GLASS_BORDER);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS), border);
        }

        boolean headerHover = isHovered(mouseX, mouseY, x, y, width, HEADER_HEIGHT);
        float targetHover = headerHover ? 0.08f : 0f;
        hoverAlpha += (targetHover - hoverAlpha) * 0.3f;
        if (hoverAlpha > 0.01f) {
            try (Paint hover = new Paint()) {
                hover.setColor(0x1AFFFFFF);
                canvas.drawRRect(RRect.makeXYWH(x, y, width, HEADER_HEIGHT, CORNER_RADIUS), hover);
            }
        }

        SkiaRenderer.drawCenteredIcon(canvas, category.getIcon(), x + 20, y + HEADER_HEIGHT / 2, ICON_SIZE, TEXT_PRIMARY);
        try (Paint textPaint = new Paint().setColor(TEXT_PRIMARY)) {
            Font font = FontManager.INSTANCE.getTextFont(FONT_SIZE_HEADER);
            FontMetrics metrics = font.getMetrics();
            float textY = y + HEADER_HEIGHT / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(category.getName(), x + 36, textY, font, textPaint);
        }

        canvas.save();
        canvas.translate(x + width - 18, y + HEADER_HEIGHT / 2);
        canvas.rotate(arrowRotation);
        SkiaRenderer.drawCenteredIcon(canvas, Icon.EXPAND_LESS, 0, 0, ICON_SIZE - 2, TEXT_SECONDARY);
        canvas.restore();

        if (animationProgress > 0.01f) {
            canvas.save();
            canvas.clipRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS));
            canvas.clipRect(Rect.makeXYWH(x, y + HEADER_HEIGHT, width, currentContentHeight));
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                btn.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += btn.getTotalHeight();
            }
            canvas.restore();
        }

        this.height = totalHeight;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, x, y, width, HEADER_HEIGHT)) {
            if (button == 0) {
                dragging = true;
                dragX = (float) mouseX - x;
                dragY = (float) mouseY - y;
                return true;
            } else if (button == 1) {
                expanded = !expanded;
                return true;
            }
        }
        if (expanded && animationProgress > 0.9f) {
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                if (btn.mouseClicked(mouseX, mouseY, button, x, yOffset)) return true;
                yOffset += btn.getTotalHeight();
            }
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        if (expanded) {
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                btn.mouseReleased(mouseX, mouseY, button, x, yOffset);
                yOffset += btn.getTotalHeight();
            }
        }
    }

    public boolean handleKeyPress(int keyCode) {
        if (expanded) {
            for (ModuleButton btn : moduleButtons) {
                if (btn.handleKeyPress(keyCode)) return true;
            }
        }
        return false;
    }

    public void resetDrag() {
        dragging = false;
    }

    private boolean isHovered(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void clampToScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) return;
        int sw = mc.screen.width, sh = mc.screen.height;
        x = Math.clamp(x, 0, sw - width);
        y = Math.clamp(y, 0, sh - height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}