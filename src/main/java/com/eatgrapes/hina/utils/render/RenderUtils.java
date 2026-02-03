/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;

public class RenderUtils {
    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines_no_depth",
            1536,
            false,
            false,
            RenderPipelines.LINES,
            RenderType.CompositeState.builder()
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .createCompositeState(false)
    );

    public static void drawBox(PoseStack poseStack, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a, float thickness) {
        RenderSystem.lineWidth(thickness);
        Matrix4f matrix = poseStack.last().pose();
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
            vertexConsumer.addVertex(matrix, p[0], p[1], p[2]).setColor(r, g, b, a).setNormal(0, 0, 0);
        }
    }
}