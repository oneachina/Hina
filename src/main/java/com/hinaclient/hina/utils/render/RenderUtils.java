/*
 * Hina Client
 * Copyright (C) 2026 Hina Client
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.hinaclient.hina.utils.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

public class RenderUtils {
    public static final RenderType QUADS_NO_DEPTH = RenderType.create(
            "quads_no_depth",
            RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.MAIN_TARGET)
                    .createRenderSetup()
    );

    public static void drawBox(PoseStack poseStack, VertexConsumer buffer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a, float thickness) {
        Matrix4f matrix = poseStack.last().pose();
        float t = thickness / 100f;

        float x1f = (float) x1, y1f = (float) y1, z1f = (float) z1;
        float x2f = (float) x2, y2f = (float) y2, z2f = (float) z2;

        drawQuadEdge(matrix, buffer, x1f, y1f, z1f, x2f, y1f, z1f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y1f, z1f, x2f, y1f, z2f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y1f, z2f, x1f, y1f, z2f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x1f, y1f, z2f, x1f, y1f, z1f, t, r, g, b, a);

        drawQuadEdge(matrix, buffer, x1f, y2f, z1f, x2f, y2f, z1f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y2f, z1f, x2f, y2f, z2f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y2f, z2f, x1f, y2f, z2f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x1f, y2f, z2f, x1f, y2f, z1f, t, r, g, b, a);

        drawQuadEdge(matrix, buffer, x1f, y1f, z1f, x1f, y2f, z1f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y1f, z1f, x2f, y2f, z1f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x2f, y1f, z2f, x2f, y2f, z2f, t, r, g, b, a);
        drawQuadEdge(matrix, buffer, x1f, y1f, z2f, x1f, y2f, z2f, t, r, g, b, a);
    }

    private static void drawQuadEdge(Matrix4f matrix, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float t, float r, float g, float b, float a) {
        if (x1 != x2) {
            buffer.addVertex(matrix, x1, y1 - t, z1 - t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x2, y1 - t, z1 - t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x2, y1 + t, z1 + t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1, y1 + t, z1 + t).setColor(r, g, b, a);
        } else if (y1 != y2) {
            buffer.addVertex(matrix, x1 - t, y1, z1 - t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 - t, y2, z1 - t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 + t, y2, z1 + t).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 + t, y1, z1 + t).setColor(r, g, b, a);
        } else {
            buffer.addVertex(matrix, x1 - t, y1 - t, z1).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 - t, y1 - t, z2).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 + t, y1 + t, z2).setColor(r, g, b, a);
            buffer.addVertex(matrix, x1 + t, y1 + t, z1).setColor(r, g, b, a);
        }
    }
}