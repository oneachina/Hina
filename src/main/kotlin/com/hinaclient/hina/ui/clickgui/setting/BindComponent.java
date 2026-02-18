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
import com.hinaclient.hina.setting.Setting;
import com.hinaclient.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 13:51
 */
public class BindComponent extends Component {
    private final Module module;
    private boolean listening;
    private float currentX, currentY;

    public BindComponent(Module module, Setting<?> setting, float width, float height) {
        super(setting, width, height);
        this.module = module;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        this.currentX = x;
        this.currentY = y;

        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);

            if (isHovered(mouseX, mouseY, x, y)) {
                paint.setColor(0x1AFFFFFF);
                canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
            }

            String keyName = module.getKey() == -1 ? "NONE" : GLFW.glfwGetKeyName(module.getKey(), 0);
            if (keyName == null) keyName = "Key: " + module.getKey();

            String text = listening ? "[Listening...]" : "Bind: " + keyName.toUpperCase();

            try (Paint textPaint = new Paint()) {
                textPaint.setColor(listening ? 0xFF55FF55 : 0xFFAAAAAA);
                canvas.drawString(text, x + 10, y + height / 2 + 5, FontManager.INSTANCE.getTextFont(14), textPaint);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.listening = !this.listening;
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

    @Override
    public float getHeight() {
        return height;
    }
}
