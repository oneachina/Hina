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
import java.util.ArrayList;
import java.util.List;

public class Panel {
    private final Category category;
    private float x, y;
    private float width;
    private float height;
    private boolean expanded;
    private boolean dragging;
    private float dragX, dragY;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private float animationProgress;
    private float arrowRotation;
    private final float HEADER_HEIGHT = 32; 
    private final float CORNER_RADIUS = 8;
    private final float FONT_SIZE_HEADER = 15;
    private final float ICON_SIZE = 16;
    private final float MODULE_BUTTON_HEIGHT = 26;

    public Panel(Category category, float x, float y, float width) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width + 30; 
        this.height = HEADER_HEIGHT;
        this.expanded = true;
        this.animationProgress = 1.0f;
        for (Module module : HinaClient.INSTANCE.moduleManager.getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(module, this.width, MODULE_BUTTON_HEIGHT));
        }
    }

    public void render(Canvas canvas, int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        float targetProgress = expanded ? 1.0f : 0.0f;
        animationProgress += (targetProgress - animationProgress) * 0.2f;
        float targetRotation = expanded ? 180f : 0f;
        arrowRotation += (targetRotation - arrowRotation) * 0.2f;
        float modulesHeight = 0;
        if (animationProgress > 0.01f) {
            for (ModuleButton btn : moduleButtons) modulesHeight += btn.getTotalHeight();
        }
        float currentContentHeight = modulesHeight * animationProgress;
        float totalHeight = HEADER_HEIGHT + currentContentHeight;
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS), paint);
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(0xFFFFFFFF);
            float centerY = y + HEADER_HEIGHT / 2;
            SkiaRenderer.drawCenteredIcon(canvas, category.getIcon(), x + 18, centerY, ICON_SIZE, 0xFFFFFFFF);
            Font font = FontManager.INSTANCE.getTextFont(FONT_SIZE_HEADER);
            FontMetrics metrics = font.getMetrics();
            float textY = centerY - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(category.getName(), x + 34, textY, font, textPaint);
            canvas.save();
            float arrowX = x + width - 15;
            canvas.translate(arrowX, centerY);
            canvas.rotate(arrowRotation);
            SkiaRenderer.drawCenteredIcon(canvas, Icon.EXPAND_LESS, 0, 0, ICON_SIZE - 2, 0xFFFFFFFF);
            canvas.restore();
        }
        if (animationProgress > 0.01f) {
            canvas.save();
            canvas.clipRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS), true);
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
                dragX = (float)mouseX - x;
                dragY = (float)mouseY - y;
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
                if (btn.mouseReleased(mouseX, mouseY, button, x, yOffset)) return;
                yOffset += btn.getTotalHeight();
            }
        }
    }
    
    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean handleKeyPress(int keyCode) {
        if (expanded) {
            for (ModuleButton btn : moduleButtons) {
                if (btn.handleKeyPress(keyCode)) return true;
            }
        }
        return false;
    }
}
