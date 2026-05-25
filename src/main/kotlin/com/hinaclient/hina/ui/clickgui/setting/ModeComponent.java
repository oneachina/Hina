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

package com.hinaclient.hina.ui.clickgui.setting;

import com.hinaclient.hina.setting.ModeSetting;
import com.hinaclient.hina.skia.SkiaRenderer;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.skia.font.Icon;
import com.hinaclient.hina.ui.clickgui.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import java.util.List;

public class ModeComponent extends Component {
    private final ModeSetting modeSetting;
    private float currentX, currentY;
    private boolean expanded = false;
    private float dropdownProgress = 0f;
    private final float OPTION_HEIGHT = 26;

    public ModeComponent(ModeSetting setting, float width, float height) {
        super(setting, width, height);
        this.modeSetting = setting;
    }

    @Override
    public void update() {
        float target = expanded ? 1f : 0f;
        dropdownProgress += (target - dropdownProgress) * 0.2f;
        if (Math.abs(target - dropdownProgress) < 0.001f) dropdownProgress = target;
        super.update();
    }

    @Override
    public float getHeight() {
        float base = super.getHeight();
        if (!setting.isVisible() || base < 0.01f) return 0;
        if (dropdownProgress > 0.01f) {
            return base + (modeSetting.getModes().size() * OPTION_HEIGHT) * dropdownProgress;
        }
        return base;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;

        try (Paint bg = new Paint()) {
            bg.setColor(0x40FFFFFF);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 8), bg);
        }

        try (Paint textPaint = new Paint().setColor(0xFFEEEEEE)) {
            Font font = FontManager.INSTANCE.getTextFont(13);
            FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(setting.getName(), x + 12, textY, font, textPaint);
        }

        String value = modeSetting.getValue();
        try (Paint valPaint = new Paint().setColor(0xFFFFFFFF)) {
            Font font = FontManager.INSTANCE.getTextFont(12);
            float valW = font.measureTextWidth(value, valPaint);
            canvas.drawString(value, x + width - valW - 28, y + height / 2 + 4, font, valPaint);
        }
        SkiaRenderer.drawCenteredIcon(canvas, expanded ? Icon.ARROW_CIRCLE_UP : Icon.ARROW_CIRCLE_DOWN,
                x + width - 18, y + height / 2, 12, 0xCCFFFFFF);

        if (dropdownProgress > 0.01f) {
            List<String> modes = modeSetting.getModes();
            float listHeight = modes.size() * OPTION_HEIGHT;
            float listY = y + height;
            canvas.save();
            canvas.clipRect(Rect.makeXYWH(x, listY, width, listHeight * dropdownProgress));

            for (int i = 0; i < modes.size(); i++) {
                String mode = modes.get(i);
                boolean selected = mode.equals(modeSetting.getValue());
                float optY = listY + i * OPTION_HEIGHT;
                try (Paint optBg = new Paint()) {
                    optBg.setColor(selected ? 0x30FFFFFF : 0x10FFFFFF);
                    canvas.drawRRect(RRect.makeXYWH(x, optY, width, OPTION_HEIGHT, 6), optBg);
                }
                try (Paint optText = new Paint().setColor(selected ? 0xFFFFFFFF : 0xCCFFFFFF)) {
                    Font font = FontManager.INSTANCE.getTextFont(12);
                    canvas.drawString(mode, x + 16, optY + OPTION_HEIGHT / 2 + 4, font, optText);
                }
            }
            canvas.restore();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        if (isHovered(mouseX, mouseY, currentX, currentY, width, height)) {
            expanded = !expanded;
            return true;
        }
        if (expanded && dropdownProgress > 0.8f) {
            float startY = currentY + height;
            List<String> modes = modeSetting.getModes();
            for (int i = 0; i < modes.size(); i++) {
                float optY = startY + i * OPTION_HEIGHT;
                if (mouseX >= currentX && mouseX <= currentX + width && mouseY >= optY && mouseY <= optY + OPTION_HEIGHT) {
                    modeSetting.setValue(modes.get(i));
                    expanded = false;
                    return true;
                }
            }
        }
        return false;
    }
}