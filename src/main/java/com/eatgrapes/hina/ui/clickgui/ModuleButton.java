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
import org.lwjgl.glfw.GLFW;

public class ModuleButton {
    private final Module module;
    private final float width;
    private final float height;
    private final List<Component> components = new ArrayList<>();
    private boolean extended = false;
    private float enableProgress = 0f;
    private float extensionProgress = 0f;

    private boolean binding = false;
    private float bindingAnim = 0f;

    private final float SETTING_HEIGHT = 26;

    public ModuleButton(Module module, float width, float height) {
        this.module = module;
        this.width = width;
        this.height = height;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) components.add(new CheckboxComponent((BooleanSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof NumberSetting) components.add(new SliderComponent((NumberSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ModeSetting) components.add(new ModeComponent((ModeSetting) setting, width, SETTING_HEIGHT));
            else if (setting instanceof ColorSetting) components.add(new ColorComponent((ColorSetting) setting, width, 100));
        }
    }

    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        float targetEnable = module.isEnabled() ? 1f : 0f;
        enableProgress += (targetEnable - enableProgress) * 0.15f;

        float targetExtend = extended ? 1f : 0f;
        extensionProgress += (targetExtend - extensionProgress) * 0.15f;

        if (binding) bindingAnim += 0.1f;

        try (Paint paint = new Paint()) {
            paint.setColor(0xFF1E1E1E);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);

            if (enableProgress > 0.01f) {
                int themeColor = ClickGuiModule.getThemeColor();
                int alpha = (int) (enableProgress * 255);
                paint.setColor((alpha << 24) | (themeColor & 0xFFFFFF));
                canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
            }

            try (Paint textPaint = new Paint()) {
                textPaint.setColor(0xFFFFFFFF);
                canvas.drawString(module.getName(), x + 10, y + height / 2 + 5, FontManager.INSTANCE.getTextFont(16), textPaint);

                String display;
                if (binding) {
                    display = "[ Listening... ]";
                } else if (module.getKey() <= 0) {
                    display = "";
                } else {
                    // 修复点：检查 glfwGetKeyName 是否为 null
                    String keyName = GLFW.glfwGetKeyName(module.getKey(), 0);
                    if (keyName == null) {
                        // 如果是特殊按键，手动处理常见按键名或直接显示 ID
                        display = "[" + getKeyNameById(module.getKey()) + "]";
                    } else {
                        display = "[" + keyName.toUpperCase() + "]";
                    }
                }

                if (!display.isEmpty()) {
                    if (binding) {
                        int alpha = (int) (155 + Math.sin(bindingAnim) * 100);
                        textPaint.setAlpha(alpha);
                    } else {
                        textPaint.setColor(0xFFAAAAAA);
                    }
                    float dw = FontManager.INSTANCE.getTextFont(12).measureTextWidth(display, textPaint);
                    canvas.drawString(display, x + width - dw - 10, y + height / 2 + 4, FontManager.INSTANCE.getTextFont(12), textPaint);
                }
            }
        }

        if (extensionProgress > 0.01f) {
            float yOffset = y + height;
            canvas.save();
            canvas.clipRect(Rect.makeXYWH(x, y + height, width, getSettingsHeight() * extensionProgress));
            for (Component comp : components) {
                if (!comp.getSetting().isVisible()) continue;
                comp.render(canvas, x, yOffset, mouseX, mouseY);
                yOffset += comp.getHeight();
            }
            canvas.restore();
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            if (button == 0) {
                if (binding) {
                    binding = false;
                } else {
                    module.toggle();
                }
                return true;
            } else if (button == 1) {
                binding = !binding;
                return true;
            } else if (button == 2) {
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

    public void onKey(int key) {
        if (binding) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
                module.setKey(0);
            } else {
                module.setKey(key);
            }
            binding = false;
        }
    }

    public boolean isBinding() {
        return binding;
    }

    public float getTotalHeight() {
        return height + (extended ? getSettingsHeight() * extensionProgress : 0);
    }

    private float getSettingsHeight() {
        float h = 0;
        for (Component c : components) if (c.getSetting().isVisible()) h += c.getHeight();
        return h;
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    /**
     * @Author: oneachina
     * @link: github.com/oneachina
     */
    private String getKeyNameById(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE: return "ESC";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_BACKSPACE: return "BS";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            case GLFW.GLFW_KEY_CAPS_LOCK: return "CAPS";
            default:
                return "K" + key;
        }
    }
}