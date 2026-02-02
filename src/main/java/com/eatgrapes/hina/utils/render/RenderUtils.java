/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;

public class RenderUtils {
    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines_no_depth",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
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