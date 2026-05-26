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

import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.ui.clickgui.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import org.lwjgl.glfw.GLFW;

public class BindComponent extends Component {
    private final Module module;
    private boolean listening;
    private float currentX, currentY;

    public BindComponent(Module module, float width, float height) {
        super(null, width, height);
        this.module = module;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        this.currentX = x;
        this.currentY = y;

        try (Paint bg = new Paint()) {
            bg.setColor(0x40FFFFFF);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 8), bg);
        }

        boolean hover = isHovered(mouseX, mouseY, x, y);
        if (hover) {
            try (Paint hoverPaint = new Paint()) {
                hoverPaint.setColor(0x1AFFFFFF);
                canvas.drawRRect(RRect.makeXYWH(x, y, width, height, 8), hoverPaint);
            }
        }

        String key = module.getKey() == -1 ? "未绑定" : GLFW.glfwGetKeyName(module.getKey(), 0);
        if (key == null) key = "键位 " + module.getKey();
        String text = listening ? "按下任意键..." : "按键: " + key.toUpperCase();

        try (Paint textPaint = new Paint().setColor(listening ? 0xFF88FF88 : 0xFFEEEEEE)) {
            Font font = FontManager.INSTANCE.getTextFont(13);
            FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(text, x + 12, textY, font, textPaint);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            listening = !listening;
            return true;
        }
        return false;
    }

    public boolean onKeyPressed(int key) {
        if (listening) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                listening = false;
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                module.setKey(-1);
                listening = false;
            } else {
                module.setKey(key);
                listening = false;
            }
            return true;
        }
        return false;
    }
}