/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.skia;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceColorFormat;
import io.github.humbleui.skija.SurfaceOrigin;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class SkiaContext {
    private static DirectContext context = null;
    private static Surface surface;
    private static BackendRenderTarget renderTarget;

    public static Canvas getCanvas() {
        if (surface == null) return null;
        return surface.getCanvas();
    }
    
    public static DirectContext getContext() {
        return context;
    }

    public static void createSurface(int width, int height) {
        if (context == null) {
            context = DirectContext.makeGL();
        }
        if (surface != null) {
            surface.close();
            surface = null;
        }
        if (renderTarget != null) {
            renderTarget.close();
            renderTarget = null;
        }
        int frameBufferId = Minecraft.getInstance().getMainRenderTarget().frameBufferId;
        renderTarget = BackendRenderTarget.makeGL(width, height, 0, 8, frameBufferId, GL11.GL_RGBA8);
        surface = Surface.wrapBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888, ColorSpace.getSRGB());
    }
}