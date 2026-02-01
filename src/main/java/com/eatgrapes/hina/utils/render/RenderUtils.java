/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class RenderUtils {
    public static final RenderLayer LINES_NO_DEPTH = RenderLayer.of(
            "lines_no_depth",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.LINES_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false)
    );

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a, float thickness) {
        RenderSystem.lineWidth(thickness);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        float x1f = (float) x1, y1f = (float) y1, z1f = (float) z1;
        float x2f = (float) x2, y2f = (float) y2, z2f = (float) z2;

        float[][] v = {
                {x1f, y1f, z1f}, {x2f, y1f, z1f}, {x2f, y1f, z1f}, {x2f, y1f, z2f},
                {x2f, y1f, z2f}, {x1f, y1f, z2f}, {x1f, y1f, z2f}, {x1f, y1f, z1f},
                {x1f, y2f, z1f}, {x2f, y2f, z1f}, {x2f, y2f, z1f}, {x2f, y2f, z2f},
                {x2f, y2f, z2f}, {x1f, y2f, z2f}, {x1f, y2f, z2f}, {x1f, y2f, z1f},
                {x1f, y1f, z1f}, {x1f, y2f, z1f}, {x2f, y1f, z1f}, {x2f, y2f, z1f},
                {x2f, y1f, z2f}, {x2f, y2f, z2f}, {x1f, y1f, z2f}, {x1f, y2f, z2f}
        };

        for (float[] p : v) {
            vertexConsumer.vertex(matrix, p[0], p[1], p[2]).color(r, g, b, a).normal(0, 0, 0);
        }
    }
}