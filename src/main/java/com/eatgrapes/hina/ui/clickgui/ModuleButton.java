/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.ui.clickgui;

import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;
import com.eatgrapes.hina.setting.*;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.ui.clickgui.setting.*;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.types.Rect;
import java.util.ArrayList;
import java.util.List;

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
        // this.enableProgress = module.isEnabled() ? 1.0f : 0.0f;
        this.width = width;
        this.height = height;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) components.add(new CheckboxComponent((BooleanSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof NumberSetting) components.add(new SliderComponent((NumberSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ModeSetting) components.add(new ModeComponent((ModeSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ColorSetting) components.add(new ColorComponent((ColorSetting) setting, width, COLOR_HEIGHT));
        }

        components.add(new BindComponent(module, new BindSetting("Bind", module.getKey()), width, SETTING_HEIGHT));
    }

    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        float target = module.isEnabled() ? 1.0f : 0.0f;
        enableProgress += (target - enableProgress) * 0.2f;
        float extTarget = extended ? 1.0f : 0.0f;
        extensionProgress += (extTarget - extensionProgress) * 0.2f;
        if (enableProgress > 0.01f) {
            try (Paint fill = new Paint()) {
                fill.setMode(PaintMode.FILL);

                int themeColor = ClickGuiModule.getThemeColor();
                fill.setColor(themeColor);

                float centerX = x + width / 2;
                float currentWidth = width * enableProgress;
                canvas.drawRect(Rect.makeXYWH(centerX - currentWidth / 2, y, currentWidth, height), fill);
            }
        }
        try (Paint textPaint = new Paint()) {
            textPaint.setColor(module.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
            io.github.humbleui.skija.Font font = FontManager.INSTANCE.getTextFont(14);
            io.github.humbleui.skija.FontMetrics metrics = font.getMetrics();
            float textY = y + height / 2 - (metrics.getAscent() + metrics.getDescent()) / 2;
            canvas.drawString(module.getName(), x + 12, textY, font, textPaint);
        }
        if (extensionProgress > 0.01f) {
            float yOffset = y + height;
            canvas.save();
            float totalSettingsHeight = 0;
            for(Component c : components) totalSettingsHeight += c.getHeight();
            canvas.clipRect(Rect.makeXYWH(x, y + height, width, totalSettingsHeight * extensionProgress));
            for (Component comp : components) {
                comp.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += comp.getHeight();
            }
            canvas.restore();
        }
    }
    
    public float getTotalHeight() {
        float h = height;
        if (extensionProgress > 0.01f) {
             float settingsHeight = 0;
             for (Component comp : components) settingsHeight += comp.getHeight();
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
                if (comp.mouseReleased(mouseX, mouseY, button)) return true;
                yOffset += comp.getHeight();
            }
        }
        return false;
    }
    
    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean handleKeyPress(int keyCode) {
        if (extended) {
            for (Component comp : components) {
                if (comp instanceof BindComponent bind) {
                    if (bind.onKeyPressed(keyCode)) return true;
                }
            }
        }
        return false;
    }
}