/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.Render3DEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class HinaLevelRendererMixin {
    @Inject(method = "renderBlockOutline", at = @At("HEAD"))
    private void renderBlockOutline(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean bl, LevelRenderState levelRenderState, CallbackInfo ci) {
        EventBus.INSTANCE.post(new Render3DEvent(
                Minecraft.getInstance().getDeltaTracker(),
                poseStack,
                poseStack.last().pose(),
                bufferSource)
        );
    }
}