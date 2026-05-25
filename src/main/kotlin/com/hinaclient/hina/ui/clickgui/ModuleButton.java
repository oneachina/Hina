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

import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.module.impl.render.ClickGuiModule;
import com.hinaclient.hina.setting.*;
import com.hinaclient.hina.skia.SkiaRenderer;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.skia.font.Icon;
import com.hinaclient.hina.ui.clickgui.setting.*;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton {
    private final Module module;
    private final float width;
    private final float height;
    private final List<Component> components = new ArrayList<>();
    private boolean extended = false;
    private float enableProgress = 0f;
    private float extensionProgress = 0f;
    private float hoverAlpha = 0f;
    private final float SETTING_HEIGHT = 30;
    private final float COLOR_HEIGHT = 110;

    public ModuleButton(Module module, float width, float height) {
        this.module = module;
        this.width = width;
        this.height = height;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting)
                components.add(new CheckboxComponent((BooleanSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof NumberSetting)
                components.add(new SliderComponent((NumberSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ModeSetting)
                components.add(new ModeComponent((ModeSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ColorSetting)
                components.add(new ColorComponent((ColorSetting) setting, width, COLOR_HEIGHT));
        }
        components.add(new BindComponent(module, width, SETTING_HEIGHT));
    }

    public void update() {
        float target = module.isEnabled() ? 1.0f : 0.0f;
        enableProgress += (target - enableProgress) * 0.2f;
        if (Math.abs(target - enableProgress) < 0.001f) enableProgress = target;

        float extTarget = extended ? 1.0f : 0.0f;
        extensionProgress += (extTarget - extensionProgress) * 0.2f;
        if (Math.abs(extTarget - extensionProgress) < 0.001f) extensionProgress = extTarget;

        for (Component comp : components) comp.update();
    }

    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        boolean hover = isHovered(mouseX, mouseY, x, y, width, height);
        float targetHover = hover ? 0.1f : 0f;
        hoverAlpha += (targetHover - hoverAlpha) * 0.3f;

        try (Paint bg = new Paint()) {
            bg.setColor(0x80FFFFFF);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 10), bg);
        }
        if (hoverAlpha > 0.01f) {
            try (Paint hoverPaint = new Paint()) {
                hoverPaint.setColor(0x33FFFFFF);
                canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 10), hoverPaint);
            }
        }

        if (enableProgress > 0.01f) {
            try (Paint fill = new Paint()) {
                int theme = ClickGuiModule.getThemeColor();
                fill.setColor(theme);
                float fillWidth = width * enableProgress;
                canvas.drawRRect(RRect.makeXYWH(x, y, fillWidth, height, 10), fill);
            }
        }

        try (Paint accent = new Paint()) {
            accent.setColor(ClickGuiModule.getThemeColor());
            canvas.drawRRect(RRect.makeXYWH(x, y + 4, 3, height - 8, 1.5f), accent);
        }
        try (Paint textPaint = new Paint().setColor(module.isEnabled() ? 0xFFFFFFFF : 0xCCFFFFFF)) {
            Font font = FontManager.INSTANCE.getTextFont(14);
            FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(module.getName(), x + 14, textY, font, textPaint);
        }

        String keyName = module.getKey() == -1 ? "无" : org.lwjgl.glfw.GLFW.glfwGetKeyName(module.getKey(), 0);
        if (keyName == null) keyName = "键" + module.getKey();
        try (Paint keyPaint = new Paint().setColor(0xAAFFFFFF)) {
            Font font = FontManager.INSTANCE.getTextFont(11);
            float keyWidth = font.measureTextWidth(keyName, keyPaint);
            canvas.drawString(keyName, x + width - keyWidth - 24, y + height / 2 + 3, font, keyPaint);
        }

        if (!components.isEmpty()) {
            SkiaRenderer.drawCenteredIcon(canvas, Icon.SETTINGS, x + width - 16, y + height / 2, 12, 0xCCFFFFFF);
        }

        if (extensionProgress > 0.01f) {
            float yOffset = y + height;
            canvas.save();
            float totalSettingsHeight = 0;
            for (Component c : components) totalSettingsHeight += c.getHeight();
            canvas.clipRect(Rect.makeXYWH(x, y + height, width, totalSettingsHeight * extensionProgress));
            for (Component comp : components) {
                comp.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += comp.getHeight();
            }
            canvas.restore();
        }
    }

    public float getTotalHeight() {
        float h = height;
        if (extensionProgress > 0.01f) {
            float settingsHeight = 0;
            for (Component comp : components) settingsHeight += comp.getHeight();
            h = height + settingsHeight * extensionProgress;
        }
        return h;
    }

    public boolean mouseClicked(double mx, double my, int btn, float x, float y) {
        if (isHovered(mx, my, x, y, width, height)) {
            if (btn == 0) {
                module.toggle();
                return true;
            } else if (btn == 1) {
                extended = !extended;
                return true;
            }
        }
        if (extended) {
            float yOffset = y + height;
            for (Component comp : components) {
                if (comp.mouseClicked(mx, my, btn)) return true;
                yOffset += comp.getHeight();
            }
        }
        return false;
    }

    public void mouseReleased(double mx, double my, int btn, float x, float y) {
        if (extended) {
            float yOffset = y + height;
            for (Component comp : components) {
                comp.mouseReleased(mx, my, btn);
                yOffset += comp.getHeight();
            }
        }
    }

    public boolean handleKeyPress(int keyCode) {
        if (extended) {
            for (Component comp : components) {
                if (comp instanceof BindComponent bind && bind.onKeyPressed(keyCode))
                    return true;
            }
        }
        return false;
    }

    private boolean isHovered(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}