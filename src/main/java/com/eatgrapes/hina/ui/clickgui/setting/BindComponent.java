package com.eatgrapes.hina.ui.clickgui.setting;

import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.utils.KeyUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 13:51
 */
public class BindComponent extends Component {
    private final Module module;
    private boolean listening;
    private float animationHover = 0f;

    public BindComponent(Module module, float width, float height) {
        super(null, width, height);
        this.module = module;
    }

    @Override
    public void render(Canvas canvas, float x, float y, int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY, x, y);
        animationHover += ((hovered ? 1f : 0f) - animationHover) * 0.15f;

        try (Paint paint = new Paint()) {
            paint.setColor(0xCC1A1A1A);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(x, y, width, height), paint);

            if (animationHover > 0.01f) {
                paint.setColor(0xFFFFFFFF);
                paint.setAlphaf(animationHover * 0.05f);
                canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(x, y, width, height), paint);
            }

            String text = listening ? "[Listening...]" : "Bind: " + KeyUtil.getKeyName(module.getKey());
            try (Paint textPaint = new Paint()) {
                textPaint.setColor(listening ? 0xFF55FF55 : 0xFFAAAAAA);
                canvas.drawString(text, x + 10, y + height / 2 + 5, FontManager.INSTANCE.getTextFont(13), textPaint);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, (float)mouseX, (float)mouseY)) {
            if (button == 1) {
                listening = !listening;
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
