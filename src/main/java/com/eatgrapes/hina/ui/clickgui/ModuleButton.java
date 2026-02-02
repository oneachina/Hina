/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui;

import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.*;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.ui.clickgui.setting.*;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import java.util.ArrayList;
import java.util.List;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;

public class ModuleButton {
    private final Module module;
    private final float width;
    private final float height;
    private final List<Component> components = new ArrayList<>();
    private boolean extended = false;
    private float enableProgress = 0f;
    private float extensionProgress = 0f;
    private final float SETTING_HEIGHT = 26;
    private final float COLOR_HEIGHT = 100;

    public ModuleButton(Module module, float width, float height) {
        this.module = module;
        this.width = width;
        this.height = height;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) components.add(new CheckboxComponent((BooleanSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof NumberSetting) components.add(new SliderComponent((NumberSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ModeSetting) components.add(new ModeComponent((ModeSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ColorSetting) components.add(new ColorComponent((ColorSetting) setting, width, COLOR_HEIGHT));
        }
    }

    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        float targetEnable = module.isEnabled() ? 1.0f : 0.0f;
        enableProgress += (targetEnable - enableProgress) * 0.2f;

        float targetExtension = extended ? 1.0f : 0.0f;
        extensionProgress += (targetExtension - extensionProgress) * 0.2f;

        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);

            if (enableProgress > 0.01f) {
                paint.setColor(ClickGuiModule.getThemeColor());
                paint.setAlphaf(enableProgress * 0.2f);
                canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
            }
        }

        try (Paint textPaint = new Paint()) {
            textPaint.setColor(module.isEnabled() ? ClickGuiModule.getThemeColor() : 0xFFAAAAAA);
            canvas.drawString(module.getName(), x + 10, y + height / 2 + 5, FontManager.INSTANCE.getTextFont(14), textPaint);
        }

        if (extended || extensionProgress > 0.01f) {
            float yOffset = y + height;
            for (Component comp : components) {
                if (!comp.getSetting().isVisible()) continue;
                comp.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += comp.getHeight();
            }
        }
    }

    public float getTotalHeight() {
        float h = height;
        if (extended || extensionProgress > 0.01f) {
            float settingsHeight = 0;
            for (Component comp : components) {
                settingsHeight += comp.getHeight();
            }
            h = height + settingsHeight * extensionProgress;
        }
        return h;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            if (button == 0) {
                module.toggle();
                return true;
            } else if (button == 1) {
                extended = !extended;
                return true;
            }
        }
        if (extended) {
            float yOffset = y + height;
            for (Component comp : components) {
                if (!comp.getSetting().isVisible()) continue;
                if (comp.mouseClicked(mouseX, mouseY, button)) return true;
                yOffset += comp.getHeight();
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button, float x, float y) {
        if (extended) {
            float yOffset = y + height;
            for (Component comp : components) {
                if (!comp.getSetting().isVisible()) continue;
                if (comp.mouseReleased(mouseX, mouseY, button)) return true;
                yOffset += comp.getHeight();
            }
        }
        return false;
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}