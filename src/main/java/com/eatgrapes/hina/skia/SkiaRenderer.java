package com.eatgrapes.hina.skia;

import com.eatgrapes.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;

/**
 * @Author: oneachina
 * @Date: 2026/2/3 11:43
 */
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
