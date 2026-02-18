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
package com.hinaclient.hina.module.impl.render;

import com.hinaclient.hina.HinaClient;
import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.skia.EventSkiaDrawScene;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.BooleanSetting;
import com.hinaclient.hina.setting.ColorSetting;
import com.hinaclient.hina.skia.font.FontManager;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

public class ModuleList extends Module {
    private final BooleanSetting bgenb = new BooleanSetting("BackGround", false);
    private final BooleanSetting iconenb = new BooleanSetting("Icon", true);
    private final BooleanSetting useThemeColor = new BooleanSetting("Use Theme Color", true);
    private final ColorSetting textColor = new ColorSetting("Text Color", 0xFFFFFFFF);
    private final Map<Module, Float> moduleProgressMap = new HashMap<>();

    public ModuleList() {
        super("ModuleList", Category.RENDER);
        addSetting(bgenb);
        addSetting(iconenb);
        addSetting(useThemeColor);
        addSetting(textColor.setVisibility(() -> !useThemeColor.getValue()));
    }

    @EventListener
    public void onDraw(EventSkiaDrawScene event) {
        if (!FontManager.INSTANCE.isInitialized()) return;

        Canvas canvas = event.getCanvas();
        Font textFont = FontManager.INSTANCE.getTextFont(16f);
        Font iconFont = FontManager.INSTANCE.getIconFont(18f);

        for (Module m : HinaClient.getINSTANCE().moduleManager.getModules()) {
            boolean enabled = m.isEnabled() && !Objects.equals(m.getName(), "ClickGUI") && !Objects.equals(m.getName(), "ModuleList");
            float target = enabled ? 1.0f : 0.0f;
            float current = moduleProgressMap.getOrDefault(m, 0.0f);

            if (Math.abs(current - target) > 0.001f) {
                current += (target - current) * 0.15f;
                moduleProgressMap.put(m, current);
            } else {
                current = target;
                if (current <= 0.0f) moduleProgressMap.remove(m);
            }
        }

        List<Module> displayModules = moduleProgressMap.keySet().stream()
                .sorted(Comparator.comparingDouble((Module m) -> getWidth(m, textFont, iconFont)).reversed())
                .toList();

        float yOffset = (float) getY();
        float xOffset = (float) getX();
        float screenWidth = (float) client.getWindow().getScreenWidth();

        int color;
        if (useThemeColor.getValue()) {
            color = ClickGuiModule.getThemeColor();
        } else {
            color = textColor.getValue();
        }

        for (Module m : displayModules) {
            float current = moduleProgressMap.getOrDefault(m, 0.0f);
            if (current <= 0.0f) continue;

            float width = getWidth(m, textFont, iconFont);
            float height = 22;
            float x = xOffset;
            float y = yOffset;

            canvas.save();
            canvas.translate(x, y);
            canvas.scale(current, current);

            try (Paint bgPaint = new Paint().setColor(0x801A1A1A);
                 Paint textPaint = new Paint().setColor(color);
                 Paint accentPaint = new Paint().setColor(color)) {
                canvas.drawRRect(RRect.makeXYWH(0, 0, width, height, 4), bgPaint);
                canvas.drawRect(Rect.makeXYWH(0, 0, 3, height), accentPaint);

                float textX = 8;
                float textY = height / 2f + textFont.getMetrics().getCapHeight() / 2f - 2;

                if (iconenb.getValue()) {
                    String icon = m.getCategory().getIcon();
                    canvas.drawString(icon, textX, textY + 1, iconFont, textPaint);
                    textX += 20;
                }

                canvas.drawString(m.getName(), textX, textY, textFont, textPaint);
            }

            canvas.restore();
            yOffset += height + 2;
        }
    }

    private float getWidth(Module m, Font textFont, Font iconFont) {
        try (TextLine nameLine = TextLine.make(m.getName(), textFont);
             TextLine iconLine = TextLine.make(m.getCategory().getIcon(), iconFont)) {
            return nameLine.getWidth() + (iconenb.getValue() ? iconLine.getWidth() + 16 : 10);
        }
    }
}
