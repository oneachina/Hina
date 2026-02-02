package com.eatgrapes.hina.ui.clickgui.setting;

import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.Setting;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.utils.KeyUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 13:51
 */
public class BindComponent extends Component {
    private final Module module;
    private boolean listening;
    private float animationHover = 0f;

    public BindComponent(Module module, Setting<?> setting, float width, float height) {
        super(setting, width, height);
        this.module = module;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        try (Paint paint = new Paint()) {
            // 背景
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);

            if (isHovered(mouseX, mouseY, x, y)) {
                paint.setColor(0x1AFFFFFF);
                canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
            }

            String keyName = module.getKey() == -1 ? "NONE" : GLFW.glfwGetKeyName(module.getKey(), 0);
            if (keyName == null) keyName = "Key: " + module.getKey(); // 处理特殊键

            String text = listening ? "[Listening...]" : "Bind: " + keyName.toUpperCase();

            try (Paint textPaint = new Paint()) {
                textPaint.setColor(listening ? 0xFF55FF55 : 0xFFAAAAAA);
                canvas.drawString(text, x + 10, y + height / 2 + 5, FontManager.INSTANCE.getTextFont(14), textPaint);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, (float)mouseX, (float)mouseY)) {
            if (button == 1) {
                this.listening = !this.listening;
                return true;
            }
        }
        return false;
    }

    public boolean onKeyPressed(int key) {
        if (listening) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                listening = false; // 退出监听
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                module.setKey(-1); // 重置为 NONE
                listening = false;
            } else {
                module.setKey(key);
                listening = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public float getHeight() {
        return height;
    }
}
