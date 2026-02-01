/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.ClientTickEvent;
import com.eatgrapes.hina.skia.SkiaContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class HinaMinecraftClientMixin {
    @Shadow @Final private Window window;
    @Shadow protected abstract String getWindowTitle();

    /**
     * @author Eatgrapes
     * @reason Custom Window Title
     */
    @Overwrite
    public void updateWindowTitle() {
        this.window.setTitle("Hina Client for " + getWindowTitle());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        SkiaContext.createSurface(window.getWidth(), window.getHeight());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onClientTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(new ClientTickEvent());
    }
}