/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui.setting;

import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;

public class ModeComponent extends Component {
    private final ModeSetting modeSetting;
    private float currentX, currentY;
    
    public ModeComponent(ModeSetting setting, float width, float height) {
        super(setting, width, height);
        this.modeSetting = setting;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        }
        try (Paint textPaint = new Paint()) {
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            io.github.humbleui.skija.FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            textPaint.setColor(0xFFAAAAAA);
            canvas.drawString(setting.getName() + ":", x + 8, textY, font, textPaint);
            textPaint.setColor(0xFFFFFFFF);
            String mode = modeSetting.getValue();
            float modeWidth = font.measureTextWidth(mode, textPaint);
            canvas.drawString(mode, x + width - 8 - modeWidth, textY, font, textPaint);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == 0) {
            modeSetting.cycle();
            return true;
        }
        return false;
    }
}