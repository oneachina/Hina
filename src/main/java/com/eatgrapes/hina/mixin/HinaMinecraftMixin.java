/**
 * @Author: Eatgrapes, oneachina
 * @link: github.com/oneachina
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.ClientTickEvent;
import com.eatgrapes.hina.skia.SkiaContext;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class HinaMinecraftMixin {
    @Shadow @Final private Window window;

    @Shadow protected abstract String createTitle();

    /**
     * @author: Eatgrapes
     * @reason Custom Window Title
     */
    @Overwrite
    public void updateTitle() { // Mojang: updateTitle
        this.window.setTitle("Hina Client for " + createTitle());
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