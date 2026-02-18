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

package com.hinaclient.hina.skia;

import com.hinaclient.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;

public class SkiaRenderer {
    @SuppressWarnings("unused")
    public static void drawIcon(Canvas canvas, String iconCode, float x, float y, float size, int color) {
        try (Paint paint = new Paint()) {
            paint.setColor(color);
            canvas.drawString(iconCode, x, y, FontManager.INSTANCE.getIconFont(size), paint);
        }
    }

    public static void drawCenteredIcon(Canvas canvas, String iconCode, float centerX, float centerY, float size, int color) {
        try (Paint paint = new Paint()) {
            paint.setColor(color);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getIconFont(size);
            Rect bounds = font.measureText(iconCode, paint);
            canvas.drawString(iconCode, centerX - bounds.getLeft() - bounds.getWidth() / 2, centerY - bounds.getTop() - bounds.getHeight() / 2, font, paint);
        }
    }
}
