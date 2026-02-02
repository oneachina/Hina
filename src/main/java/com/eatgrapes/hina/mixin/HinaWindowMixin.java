/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.skia.SkiaContext;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class HinaWindowMixin {
    @Inject(method = "onFramebufferResize", at = @At("RETURN"))
    private void onFramebufferResize(long window, int width, int height, CallbackInfo ci) {
        SkiaContext.createSurface(width, height);
    }
}
