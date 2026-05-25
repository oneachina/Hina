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

import com.hinaclient.hina.module.impl.render.ClickGuiModule;
import com.hinaclient.hina.setting.NumberSetting;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.ui.clickgui.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;

public class SliderComponent extends Component {
    private final NumberSetting numSetting;
    private float currentX, currentY;
    private boolean dragging;

    public SliderComponent(NumberSetting setting, float width, float height) {
        super(setting, width, height);
        this.numSetting = setting;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;

        if (dragging) {
            float percent = (mouseX - (x + 12)) / (width - 24);
            percent = Math.clamp(percent, 0, 1);
            double val = numSetting.getMin() + (numSetting.getMax() - numSetting.getMin()) * percent;
            if (numSetting.getIncrement() > 0)
                val = Math.round(val / numSetting.getIncrement()) * numSetting.getIncrement();
            numSetting.setValue(val);
        }

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

        String valStr = String.format("%.1f", numSetting.getValue());
        try (Paint valPaint = new Paint().setColor(0xCCFFFFFF)) {
            Font font = FontManager.INSTANCE.getTextFont(12);
            float valW = font.measureTextWidth(valStr, valPaint);
            canvas.drawString(valStr, x + width - valW - 12, y + height / 2 + 4, font, valPaint);
        }

        float sliderX = x + 12, sliderY = y + height - 12;
        float sliderW = width - 24, sliderH = 4;
        try (Paint track = new Paint()) {
            track.setColor(0x66FFFFFF);
            canvas.drawRRect(RRect.makeXYWH(sliderX, sliderY, sliderW, sliderH, 2), track);
        }

        double percent = (numSetting.getValue() - numSetting.getMin()) / (numSetting.getMax() - numSetting.getMin());
        float fillW = (float) (sliderW * percent);
        try (Paint fill = new Paint()) {
            fill.setColor(ClickGuiModule.getThemeColor());
            canvas.drawRRect(RRect.makeXYWH(sliderX, sliderY, fillW, sliderH, 2), fill);
        }

        float knobX = sliderX + fillW;
        float knobY = sliderY + sliderH / 2;
        try (Paint knob = new Paint()) {
            knob.setColor(0xFFFFFFFF);
            canvas.drawCircle(knobX, knobY, 7, knob);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == 0) {
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return false;
    }
}