/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.skia.EventSkiaDraw;
import com.eatgrapes.hina.event.skia.EventSkiaInit;
import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class HinaWindowMixin {
    @Inject(method = "onFramebufferResize", at = @At("RETURN"))
    private void onFramebufferResize(long window, int width, int height, CallbackInfo ci) {
        int finalWidth = Math.max(width, 1);
        int finalHeight = Math.max(height, 1);

        EventBus.INSTANCE.post(new EventSkiaInit(finalWidth, finalHeight));
    }

    @Inject(method = "updateDisplay", at = @At("HEAD"))
    private void onUpdateDisplay(TracyFrameCapture capturer, CallbackInfo ci) {
        EventBus.INSTANCE.post(EventSkiaDraw.INSTANCE);
    }
}
