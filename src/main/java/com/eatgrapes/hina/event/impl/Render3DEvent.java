/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.event.impl;

import com.eatgrapes.hina.event.Event;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class Render3DEvent extends Event {
    private final RenderTickCounter tickCounter;
    private final MatrixStack matrixStack;
    private final Matrix4f projectionMatrix;
    private final VertexConsumerProvider.Immediate vertexConsumers;

    public Render3DEvent(RenderTickCounter tickCounter, MatrixStack matrixStack, Matrix4f projectionMatrix, VertexConsumerProvider.Immediate vertexConsumers) {
        this.tickCounter = tickCounter;
        this.matrixStack = matrixStack;
        this.projectionMatrix = projectionMatrix;
        this.vertexConsumers = vertexConsumers;
    }

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public VertexConsumerProvider.Immediate getVertexConsumers() {
        return vertexConsumers;
    }
}
