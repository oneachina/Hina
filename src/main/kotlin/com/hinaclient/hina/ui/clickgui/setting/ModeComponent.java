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
import com.hinaclient.hina.skia.font.FontManager;
import com.hinaclient.hina.skia.font.Icon;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;

public class ModeComponent extends Component {
    private final ModeSetting modeSetting;
    private float currentX, currentY;
    private boolean expanded = false;
    private float dropdownProgress = 0f;
    private final float OPTION_HEIGHT = 16f;

    public ModeComponent(ModeSetting setting, float width, float height) {
        super(setting, width, height);
        this.modeSetting = setting;
    }

    @Override
    public void update() {
        if (!setting.isVisible()) {
            dropdownProgress = 0f;
        } else {
            float target = (expanded && modeSetting.isMulti()) ? 1f : 0f;
            float diff = target - dropdownProgress;
            if (Math.abs(diff) < 0.001f) {
                dropdownProgress = target;
            } else {
                dropdownProgress += diff * 0.2f;
            }
        }
        super.update();
    }

    @Override
    public float getHeight() {
        float baseHeight = super.getHeight();
        if (!setting.isVisible() || baseHeight < 0.01f) return 0;

        if (modeSetting.isMulti() && dropdownProgress > 0.01f) {
            float listHeight = modeSetting.getModes().size() * OPTION_HEIGHT;
            return baseHeight + (listHeight * dropdownProgress);
        }
        return baseHeight;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;

        float baseHeight = height;
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, baseHeight), paint);
        }

        try (Paint textPaint = new Paint()) {
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            float textY = y + baseHeight / 2 + 4;

            textPaint.setColor(0xFFAAAAAA);
            canvas.drawString(setting.getName() + ":", x + 6, textY, font, textPaint);

            if (!modeSetting.isMulti()) {
                textPaint.setColor(0xFFFFFFFF);
                String modeValue = modeSetting.getValue();
                if (modeValue.length() > 20) modeValue = modeValue.substring(0, 20) + "...";
                float modeWidth = font.measureTextWidth(modeValue, textPaint);
                canvas.drawString(modeValue, x + width - 6 - modeWidth, textY, font, textPaint);
            } else {
                textPaint.setColor(0xFF888888);
                String hint = expanded ? Icon.ARROW_CIRCLE_UP : Icon.ARROW_CIRCLE_DOWN;
                float hintWidth = font.measureTextWidth(hint, textPaint);
                canvas.drawString(hint, x + width - 10 - hintWidth, textY, font, textPaint);
            }
        }

        if (modeSetting.isMulti() && dropdownProgress > 0.01f) {
            float optionY = y + baseHeight;
            canvas.save();
            float totalListHeight = modeSetting.getModes().size() * OPTION_HEIGHT;
            canvas.clipRect(Rect.makeXYWH(x, optionY, width, totalListHeight * dropdownProgress));

            try (Paint listPaint = new Paint()) {
                io.github.humbleui.skija.Font subFont = FontManager.INSTANCE.getTextFont(12);
                for (String mode : modeSetting.getModes()) {
                    boolean isSelected = modeSetting.is(mode);
                    listPaint.setColor(isSelected ? 0xFF55FF55 : 0xFFAAAAAA);
                    canvas.drawString(mode, x + 12, optionY + OPTION_HEIGHT - 4, subFont, listPaint);
                    optionY += OPTION_HEIGHT;
                }
            }
            canvas.restore();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;

        if (isHovered(mouseX, mouseY, currentX, currentY, width, height)) {
            if (modeSetting.isMulti()) {
                expanded = !expanded;
            } else {
                modeSetting.cycle();
            }
            return true;
        }

        if (modeSetting.isMulti() && expanded && dropdownProgress > 0.8f) {
            float startY = currentY + height;
            for (int i = 0; i < modeSetting.getModes().size(); i++) {
                float entryY = startY + (i * OPTION_HEIGHT);
                if (mouseX >= currentX && mouseX <= currentX + width && mouseY >= entryY && mouseY <= entryY + OPTION_HEIGHT) {
                    modeSetting.toggle(modeSetting.getModes().get(i));
                    return true;
                }
            }
        }
        return false;
    }
}