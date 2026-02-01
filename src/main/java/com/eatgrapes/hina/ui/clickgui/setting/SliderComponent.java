/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui.setting;

import com.eatgrapes.hina.setting.NumberSetting;
import com.eatgrapes.hina.skia.font.FontManager;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;

public class SliderComponent extends Component {
    private final NumberSetting numSetting;
    private float currentX, currentY;
    private boolean dragging;
    
    public SliderComponent(NumberSetting setting, float width, float height) {
        super(setting, width, height);
        this.numSetting = setting;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        if (!setting.isVisible()) return;
        this.currentX = x;
        this.currentY = y;
        if (dragging) {
            float diff = mouseX - (x + 8);
            double percent = Math.min(1, Math.max(0, diff / (width - 16)));
            double newValue = numSetting.getMin() + (numSetting.getMax() - numSetting.getMin()) * percent;
            if (numSetting.getIncrement() != 0) newValue = Math.round(newValue / numSetting.getIncrement()) * numSetting.getIncrement();
            numSetting.setValue(newValue);
        }
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        }
        float sliderX = x + 8;
        float sliderY = y + height - 6;
        float sliderWidth = width - 16;
        float sliderHeight = 3;
        try (Paint paint = new Paint()) {
            paint.setColor(0xFF404040);
            canvas.drawRect(Rect.makeXYWH(sliderX, sliderY, sliderWidth, sliderHeight), paint);
        }
        double currentPercent = (numSetting.getValue() - numSetting.getMin()) / (numSetting.getMax() - numSetting.getMin());
        try (Paint paint = new Paint()) {
            paint.setColor(ClickGuiModule.getThemeColor());
            canvas.drawRect(Rect.makeXYWH(sliderX, sliderY, (float)(sliderWidth * currentPercent), sliderHeight), paint);
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(0xFFAAAAAA);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            io.github.humbleui.skija.FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2 - 2;
            canvas.drawString(setting.getName(), x + 8, textY, font, textPaint);
            String valStr = String.format("%.2f", numSetting.getValue());
            float valWidth = font.measureTextWidth(valStr, textPaint);
            canvas.drawString(valStr, x + width - 8 - valWidth, textY, font, textPaint);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!setting.isVisible()) return false;
        if (isHovered(mouseX, mouseY, currentX, currentY) && button == 0) {
            dragging = true;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return false;
    }
}