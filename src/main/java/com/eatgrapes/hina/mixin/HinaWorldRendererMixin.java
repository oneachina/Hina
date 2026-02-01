/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.Render3DEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class HinaWorldRendererMixin {
    @Inject(method = "renderTargetBlockOutline", at = @At("HEAD"))
    private void onRenderTargetBlockOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean translucent, CallbackInfo ci) {
        EventBus.INSTANCE.post(new Render3DEvent(MinecraftClient.getInstance().getRenderTickCounter(),
                matrices,
                matrices.peek().getPositionMatrix(),
                vertexConsumers)
        );
    }
}