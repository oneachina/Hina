/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.skia;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.skia.EventSkiaDraw;
import com.eatgrapes.hina.event.skia.EventSkiaDrawScene;
import com.eatgrapes.hina.event.skia.EventSkiaInit;
import com.eatgrapes.hina.skia.gl.States;
import io.github.humbleui.skija.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class SkiaContext {
    public static final SkiaContext INSTANCE = new SkiaContext();

    private final GLBackendState[] states = {
            GLBackendState.BLEND,
            GLBackendState.VERTEX,
            GLBackendState.PIXEL_STORE,
            GLBackendState.TEXTURE_BINDING,
            GLBackendState.MISC
    };

    private DirectContext context;
    private WrappedBackendRenderTarget renderTarget;
    private Surface surface;
    private Canvas canvas;

    public SkiaContext() {
        EventBus.INSTANCE.register(this);
    }

    private void initSkia(int width, int height) {
        createContext();
        createSurface(width, height);
    }

    private void createContext() {
        if (context == null) {
            context = DirectContext.makeGL();
        }
    }

    private void createSurface(int width, int height) {
        if (surface != null) surface.close();
        if (renderTarget != null) renderTarget.close();

        renderTarget = WrappedBackendRenderTarget.makeGL(
                width,
                height,
                0,
                8,
                0,
                FramebufferFormat.GR_GL_RGBA8
        );

        surface = Surface.wrapBackendRenderTarget(
                Objects.requireNonNull(context, "Context must not be null"),
                Objects.requireNonNull(renderTarget, "RenderTarget must not be null"),
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getSRGB()
        );

        canvas = surface.getCanvas();
    }

    private void draw() {
        if (context == null || surface == null) return;

        States.push();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glClearColor(0f, 0f, 0f, 0f);

        context.resetGLAll();
        drawDrawables();
        context.flushAndSubmit(surface);

        States.pop();
    }

    private void drawDrawables() {
        if (canvas != null && context != null && renderTarget != null) {
            EventBus.INSTANCE.post(new EventSkiaDrawScene(context, renderTarget, canvas));
        }
    }

    public DirectContext getContext() {
        return context;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Surface getSurface() {
        return surface;
    }

    @EventListener
    public void onInit(EventSkiaInit event) {
        initSkia(event.getWidth(), event.getHeight());
    }

    @EventListener
    public void onDraw(EventSkiaDraw event) {
        draw();
    }
}