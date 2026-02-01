/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui.setting;

import com.eatgrapes.hina.setting.BooleanSetting;
import com.eatgrapes.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;

public class CheckboxComponent extends Component {
    private final BooleanSetting boolSetting;
    private float currentX, currentY;
    private float animationProgress = 0f;
    
    public CheckboxComponent(BooleanSetting setting, float width, float height) {
        super(setting, width, height);
        this.boolSetting = setting;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;
        float target = boolSetting.getValue() ? 1.0f : 0.0f;
        animationProgress += (target - animationProgress) * 0.2f;
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(0xFFAAAAAA);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            io.github.humbleui.skija.FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(setting.getName(), x + 8, textY, font, textPaint);
        }
        float switchWidth = 32;
        float switchHeight = 12;
        float switchX = x + width - switchWidth - 10;
        float switchY = y + (height - switchHeight) / 2;
        try (Paint paint = new Paint()) {
            paint.setColor(animationProgress > 0.5f ? ClickGuiModule.getThemeColor() : 0xFF555555);
            canvas.drawRRect(RRect.makeXYWH(switchX, switchY, switchWidth, switchHeight, switchHeight / 2), paint);
        }
        float knobSize = 10;
        float knobX = switchX + 1 + (switchWidth - knobSize - 2) * animationProgress;
        float knobY = switchY + 1;
        try (Paint paint = new Paint()) {
            paint.setColor(0xFFFFFFFF);
            canvas.drawOval(Rect.makeXYWH(knobX, knobY, knobSize, knobSize), paint);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == 0) {
            boolSetting.toggle();
            return true;
        }
        return false;
    }
}