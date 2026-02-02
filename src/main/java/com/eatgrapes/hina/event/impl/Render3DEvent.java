/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.event.impl;

import com.eatgrapes.hina.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public class Render3DEvent extends Event {
    private final DeltaTracker tickCounter;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;
    private final MultiBufferSource.BufferSource vertexConsumers;

    public Render3DEvent(DeltaTracker tickCounter, PoseStack poseStack, Matrix4f projectionMatrix, MultiBufferSource.BufferSource vertexConsumers) {
        this.tickCounter = tickCounter;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.vertexConsumers = vertexConsumers;
    }

    public DeltaTracker getTickCounter() {
        return tickCounter;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public MultiBufferSource.BufferSource getVertexConsumers() {
        return vertexConsumers;
    }
}
