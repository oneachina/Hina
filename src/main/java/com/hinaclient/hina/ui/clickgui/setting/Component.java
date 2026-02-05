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

import com.hinaclient.hina.setting.Setting;
import io.github.humbleui.skija.Canvas;

public abstract class Component {
    protected final Setting<?> setting;
    protected float width;
    protected float height;
    private float visProgress = 0f;

    public Component(Setting<?> setting, float width, float height) {
        this.setting = setting;
        this.width = width;
        this.height = height;
        this.visProgress = setting.isVisible() ? 1f : 0f;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public abstract void render(Canvas canvas, float x, float y, int mouseX, int mouseY);

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public float getHeight() {
        float target = setting.isVisible() ? 1f : 0f;
        visProgress += (target - visProgress) * 0.1f;
        return height * visProgress;
    }

    public float getVisProgress() {
        return visProgress;
    }

    protected boolean isHovered(double mouseX, double mouseY, float x, float y) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}