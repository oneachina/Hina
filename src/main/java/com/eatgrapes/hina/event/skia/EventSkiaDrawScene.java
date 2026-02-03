package com.eatgrapes.hina.event.skia;

import com.eatgrapes.hina.event.Event;
import com.eatgrapes.hina.skia.WrappedBackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;

/**
 * @Author: oneachina
 * @Date: 2026/2/3 11:37
 */
public class EventSkiaDrawScene extends Event {
    private final DirectContext context;
    private final WrappedBackendRenderTarget renderTarget;
    private final Canvas canvas;

    public EventSkiaDrawScene(DirectContext context, WrappedBackendRenderTarget renderTarget, Canvas canvas) {
        this.context = context;
        this.renderTarget = renderTarget;
        this.canvas = canvas;
    }

    public DirectContext getContext() {
        return context;
    }

    public WrappedBackendRenderTarget getRenderTarget() {
        return renderTarget;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
