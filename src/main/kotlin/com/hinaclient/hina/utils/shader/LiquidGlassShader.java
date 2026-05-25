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
package com.hinaclient.hina.utils.shader;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class LiquidGlassShader {
    private static RuntimeEffect effect;
    private static long lastBgTextureId = 0;

    public static void init() {
        try (InputStream is = LiquidGlassShader.class.getResourceAsStream("/shaders/liquid_glass.sksl")) {
            if (is == null) throw new RuntimeException("Shader not found");
            String sksl = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            effect = RuntimeEffect.makeForShader(sksl);
        } catch (Exception e) {
            e.printStackTrace();
            effect = null;
        }
    }

    public static Shader makeShader(Image background, float time, Rect screenRect, Rect glassRect, float radius) {
        if (effect == null) return null;

        Data uniformData = Data.makeFromBytes(createUniforms(time, screenRect, glassRect, radius));
        Shader[] children = new Shader[]{ background.makeShader() };
        return effect.makeShader(uniformData, children, null);
    }

    private static byte[] createUniforms(float time, Rect screen, Rect glass, float radius) {
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(48).order(java.nio.ByteOrder.nativeOrder());
        buf.putFloat(time);
        buf.putFloat(screen.getLeft());
        buf.putFloat(screen.getTop());
        buf.putFloat(screen.getWidth());
        buf.putFloat(screen.getHeight());
        buf.putFloat(glass.getLeft());
        buf.putFloat(glass.getTop());
        buf.putFloat(glass.getWidth());
        buf.putFloat(glass.getHeight());
        buf.putFloat(radius);
        return buf.array();
    }
}
