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
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.skija.Shader;
import io.github.humbleui.types.Rect;
import java.awt.Color;

/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
public class ColorComponent extends Component {
    private final ColorSetting colorSetting;
    private float currentX, currentY;
    private boolean draggingHue;
    private boolean draggingSatVal;
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
        float padding = 8;
        float contentWidth = width - padding * 2;
        float hueHeight = 12;
        float pickerHeight = height - hueHeight - padding * 3 - 15;
        float pickerX = x + padding;
        float pickerY = y + 20;
        float hueX = x + padding;
        float hueY = pickerY + pickerHeight + padding;
        if (draggingHue) {
            float diff = mouseX - hueX;
            hue = Math.min(1f, Math.max(0f, diff / contentWidth));
            updateColor();
        } else if (draggingSatVal) {
            float diffX = mouseX - pickerX;
            float diffY = mouseY - pickerY;
            saturation = Math.min(1f, Math.max(0f, diffX / contentWidth));
            brightness = 1f - Math.min(1f, Math.max(0f, diffY / pickerHeight));
            updateColor();
        } else {
             updateHSB();
        }
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(0xFFAAAAAA);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            canvas.drawString(setting.getName(), x + padding, y + 14, font, textPaint);
        }
        try (Paint p = new Paint()) {
            p.setColor(Color.HSBtoRGB(hue, 1f, 1f));
            canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentWidth, pickerHeight), p);
            try (Shader s = Shader.makeLinearGradient(pickerX, pickerY, pickerX + contentWidth, pickerY, new int[]{0xFFFFFFFF, 0x00FFFFFF})) {
                try (Paint p2 = new Paint()) {
                    p2.setShader(s);
                    canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentWidth, pickerHeight), p2);
                }
            }
            try (Shader s = Shader.makeLinearGradient(pickerX, pickerY, pickerX, pickerY + pickerHeight, new int[]{0x00000000, 0xFF000000})) {
                try (Paint p2 = new Paint()) {
                    p2.setShader(s);
                    canvas.drawRect(Rect.makeXYWH(pickerX, pickerY, contentWidth, pickerHeight), p2);
                }
            }
            p.setColor(0xFFFFFFFF);
            p.setStrokeWidth(2f);
            p.setMode(PaintMode.STROKE);
            float indX = pickerX + saturation * contentWidth;
            float indY = pickerY + (1f - brightness) * pickerHeight;
            canvas.drawCircle(indX, indY, 4, p);
        }
        int[] hueColors = new int[360 / 10 + 1];
        for (int i = 0; i < hueColors.length; i++) hueColors[i] = Color.HSBtoRGB((float) i / (hueColors.length - 1), 1f, 1f);
        try (Shader s = Shader.makeLinearGradient(hueX, hueY, hueX + contentWidth, hueY, hueColors)) {
            try (Paint p = new Paint()) {
                p.setShader(s);
                canvas.drawRect(Rect.makeXYWH(hueX, hueY, contentWidth, hueHeight), p);
            }
        }
        try (Paint p = new Paint()) {
            p.setColor(0xFFFFFFFF);
            canvas.drawRect(Rect.makeXYWH(hueX + hue * contentWidth - 1, hueY - 1, 2, hueHeight + 2), p);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        float padding = 8;
        float contentWidth = width - padding * 2;
        float hueHeight = 12;
        float pickerHeight = height - hueHeight - padding * 3 - 15;
        float pickerX = currentX + padding;
        float pickerY = currentY + 20;
        float hueX = currentX + padding;
        float hueY = pickerY + pickerHeight + padding;
        if (button == 0) {
            if (mouseX >= pickerX && mouseX <= pickerX + contentWidth && mouseY >= pickerY && mouseY <= pickerY + pickerHeight) {
                draggingSatVal = true;
                return true;
            }
            if (mouseX >= hueX && mouseX <= hueX + contentWidth && mouseY >= hueY && mouseY <= hueY + hueHeight) {
                draggingHue = true;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingHue = false;
            draggingSatVal = false;
        }
        return false;
    }
}