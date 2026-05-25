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

import com.hinaclient.hina.setting.ColorSetting;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.ui.clickgui.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import java.awt.Color;

public class ColorComponent extends Component {
    private final ColorSetting colorSetting;
    private float currentX, currentY;
    private boolean draggingHue, draggingSatVal;
    private float hue, saturation, brightness;

    public ColorComponent(ColorSetting setting, float width, float height) {
        super(setting, width, height);
        this.colorSetting = setting;
        updateHSB();
    }

    private void updateHSB() {
        Color c = new Color(colorSetting.getColor());
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }

    private void updateColor() {
        colorSetting.setValue(Color.HSBtoRGB(hue, saturation, brightness));
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;

        if (!draggingHue && !draggingSatVal) updateHSB();

        float pad = 10;
        float contentW = width - pad * 2;
        float hueH = 12;
        float pickerH = height - hueH - pad * 3 - 20;
        float pickerX = x + pad, pickerY = y + 20;
        float hueX = x + pad, hueY = pickerY + pickerH + pad;

        if (draggingHue) {
            float diff = Math.clamp((mouseX - hueX) / contentW, 0f, 1f);
            hue = diff;
            updateColor();
        } else if (draggingSatVal) {
            float diffX = Math.clamp((mouseX - pickerX) / contentW, 0f, 1f);
            float diffY = Math.clamp((mouseY - pickerY) / pickerH, 0f, 1f);
            saturation = diffX;
            brightness = 1f - diffY;
            updateColor();
        }

        try (Paint bg = new Paint()) {
            bg.setColor(0x40FFFFFF);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 10), bg);
        }

        try (Paint text = new Paint().setColor(0xFFEEEEEE)) {
            Font font = FontManager.INSTANCE.getTextFont(13);
            canvas.drawString(setting.getName(), x + pad, y + 16, font, text);
        }

        try (Paint satPaint = new Paint()) {
            satPaint.setColor(Color.HSBtoRGB(hue, 1f, 1f));
            canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentW, pickerH), satPaint);

            try (Shader whiteGrad = Shader.makeLinearGradient(pickerX, pickerY, pickerX + contentW, pickerY,
                    new int[]{0xFFFFFFFF, 0x00FFFFFF})) {
                try (Paint gradPaint = new Paint().setShader(whiteGrad)) {
                    canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentW, pickerH), gradPaint);
                }
            }

            try (Shader blackGrad = Shader.makeLinearGradient(pickerX, pickerY, pickerX, pickerY + pickerH,
                    new int[]{0x00000000, 0xFF000000})) {
                try (Paint gradPaint = new Paint().setShader(blackGrad)) {
                    canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentW, pickerH), gradPaint);
                }
            }

            float indX = pickerX + saturation * contentW;
            float indY = pickerY + (1f - brightness) * pickerH;
            try (Paint ind = new Paint()) {
                ind.setColor(0xFFFFFFFF);
                ind.setMode(PaintMode.STROKE);
                ind.setStrokeWidth(2f);
                canvas.drawCircle(indX, indY, 5, ind);
            }
        }

        int[] hueColors = new int[36];
        for (int i = 0; i < hueColors.length; i++)
            hueColors[i] = Color.HSBtoRGB((float) i / (hueColors.length - 1), 1f, 1f);
        try (Shader hueShader = Shader.makeLinearGradient(hueX, hueY, hueX + contentW, hueY, hueColors)) {
            try (Paint huePaint = new Paint().setShader(hueShader)) {
                canvas.drawRRect(RRect.makeXYWH(hueX, hueY, contentW, hueH, hueH / 2), huePaint);
            }
        }

        try (Paint marker = new Paint().setColor(0xFFFFFFFF).setMode(PaintMode.STROKE).setStrokeWidth(2)) {
            canvas.drawRect(Rect.makeXYWH(hueX + hue * contentW - 2, hueY - 1, 4, hueH + 2), marker);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!setting.isVisible()) return false;
        float pad = 10, contentW = width - pad * 2, hueH = 12, pickerH = height - hueH - pad * 3 - 20;
        float pickerX = currentX + pad, pickerY = currentY + 20;
        float hueX = currentX + pad, hueY = pickerY + pickerH + pad;
        if (button == 0) {
            if (mx >= pickerX && mx <= pickerX + contentW && my >= pickerY && my <= pickerY + pickerH) {
                draggingSatVal = true;
                return true;
            }
            if (mx >= hueX && mx <= hueX + contentW && my >= hueY && my <= hueY + hueH) {
                draggingHue = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0) {
            draggingHue = false;
            draggingSatVal = false;
        }
        return false;
    }
}