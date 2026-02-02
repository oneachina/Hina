/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.skia;

import com.eatgrapes.hina.skia.font.FontManager;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL33;
import java.util.function.Consumer;

public class SkiaRenderer {

    public static void draw(Consumer<Canvas> drawingLogic) {
        if (SkiaContext.getCanvas() == null) return;
        RenderSystem.pixelStore(GlConst.GL_UNPACK_ROW_LENGTH, 0);
        RenderSystem.pixelStore(GlConst.GL_UNPACK_SKIP_PIXELS, 0);
        RenderSystem.pixelStore(GlConst.GL_UNPACK_SKIP_ROWS, 0);
        RenderSystem.pixelStore(GlConst.GL_UNPACK_ALIGNMENT, 4);
        RenderSystem.clearColor(0f, 0f, 0f, 0f);
        if (SkiaContext.getContext() != null) SkiaContext.getContext().resetGLAll();
        Canvas canvas = SkiaContext.getCanvas();
        if (canvas != null) {
             drawingLogic.accept(canvas);
        }
        if (SkiaContext.getContext() != null) SkiaContext.getContext().flush();
        BufferUploader.reset();
        GL33.glBindSampler(0, 0);
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_BLEND);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.blendEquation(GL33.GL_FUNC_ADD);
        GL33.glBlendEquation(GL33.GL_FUNC_ADD);
        RenderSystem.colorMask(true, true, true, true);
        GL11.glColorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        GL11.glDepthMask(true);
        RenderSystem.disableScissor();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.disableDepthTest();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
        RenderSystem.disableCull();
    }

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