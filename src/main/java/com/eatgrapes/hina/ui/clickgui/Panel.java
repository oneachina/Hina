/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui;

import com.eatgrapes.hina.HinaClient;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.skia.SkiaRenderer;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.skia.font.Icon;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import java.util.ArrayList;
import java.util.List;

public class Panel {
    private final Category category;
    private float x, y;
    private float width;
    private float height;
    private boolean expanded;
    private boolean dragging;
    private float dragX, dragY;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private float animationProgress;
    private float arrowRotation;
    private final float HEADER_HEIGHT = 32; 
    private final float CORNER_RADIUS = 8;
    private final float FONT_SIZE_HEADER = 15;
    private final float ICON_SIZE = 16;
    private final float MODULE_BUTTON_HEIGHT = 26;

    public Panel(Category category, float x, float y, float width) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width + 30; 
        this.height = HEADER_HEIGHT;
        this.expanded = true;
        this.animationProgress = 1.0f;
        for (Module module : HinaClient.INSTANCE.moduleManager.getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(module, this.width, MODULE_BUTTON_HEIGHT));
        }
    }

    public void render(Canvas canvas, int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        float targetProgress = expanded ? 1.0f : 0.0f;
        animationProgress += (targetProgress - animationProgress) * 0.2f;
        float targetRotation = expanded ? 180f : 0f;
        arrowRotation += (targetRotation - arrowRotation) * 0.2f;
        float modulesHeight = 0;
        if (animationProgress > 0.01f) {
            for (ModuleButton btn : moduleButtons) modulesHeight += btn.getTotalHeight();
        }
        float currentContentHeight = modulesHeight * animationProgress;
        float totalHeight = HEADER_HEIGHT + currentContentHeight;
        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS), paint);
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(0xFFFFFFFF);
            float centerY = y + HEADER_HEIGHT / 2;
            SkiaRenderer.drawCenteredIcon(canvas, category.getIcon(), x + 18, centerY, ICON_SIZE, 0xFFFFFFFF);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(FONT_SIZE_HEADER);
            io.github.humbleui.skija.FontMetrics metrics = font.getMetrics();
            float textY = centerY - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(category.getName(), x + 34, textY, font, textPaint);
            canvas.save();
            float arrowX = x + width - 15;
            canvas.translate(arrowX, centerY);
            canvas.rotate(arrowRotation);
            SkiaRenderer.drawCenteredIcon(canvas, Icon.EXPAND_LESS, 0, 0, ICON_SIZE - 2, 0xFFFFFFFF); 
            canvas.restore();
        }
        if (animationProgress > 0.01f) {
            canvas.save();
            canvas.clipRRect(RRect.makeXYWH(x, y, width, totalHeight, CORNER_RADIUS), true);
            canvas.clipRect(Rect.makeXYWH(x, y + HEADER_HEIGHT, width, currentContentHeight));
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                btn.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += btn.getTotalHeight();
            }
            canvas.restore();
        }
        this.height = totalHeight;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, x, y, width, HEADER_HEIGHT)) {
            if (button == 0) {
                dragging = true;
                dragX = (float)mouseX - x;
                dragY = (float)mouseY - y;
                return true;
            } else if (button == 1) {
                expanded = !expanded;
                return true;
            }
        }
        if (expanded && animationProgress > 0.9f) {
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                if (btn.mouseClicked(mouseX, mouseY, button, x, yOffset)) return true;
                yOffset += btn.getTotalHeight();
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        if (expanded) {
            float yOffset = y + HEADER_HEIGHT;
            for (ModuleButton btn : moduleButtons) {
                if (btn.mouseReleased(mouseX, mouseY, button, x, yOffset)) return true;
                yOffset += btn.getTotalHeight();
            }
        }
        return false;
    }
    
    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean handleKeyPress(int keyCode) {
        if (expanded) {
            for (ModuleButton btn : moduleButtons) {
                if (btn.handleKeyPress(keyCode)) return true;
            }
        }
        return false;
    }
}
