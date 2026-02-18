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

package com.hinaclient.hina.skia;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.impl.Stats;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: oneachina
 * @Date: 2026/2/3 11:18
 */
public class WrappedBackendRenderTarget extends BackendRenderTarget {
    private final int width;
    private final int height;
    private final int sampleCnt;
    private final int stencilBits;
    private final int fbId;
    private final int fbFormat;

    public WrappedBackendRenderTarget(
            int width,
            int height,
            int sampleCnt,
            int stencilBits,
            int fbId,
            int fbFormat,
            long ptr
    ) {
        super(ptr);
        this.width = width;
        this.height = height;
        this.sampleCnt = sampleCnt;
        this.stencilBits = stencilBits;
        this.fbId = fbId;
        this.fbFormat = fbFormat;
    }

    /**
     * @see BackendRenderTarget#makeGL(int, int, int, int, int, int)
     */
    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull WrappedBackendRenderTarget makeGL(
            int width,
            int height,
            int sampleCnt,
            int stencilBits,
            int fbId,
            int fbFormat
    ) {
        Stats.onNativeCall();
        return new WrappedBackendRenderTarget(
                width,
                height,
                sampleCnt,
                stencilBits,
                fbId,
                fbFormat,
                _nMakeGL(width, height, sampleCnt, stencilBits, fbId, fbFormat)
        );
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSampleCnt() { return sampleCnt; }
    public int getStencilBits() { return stencilBits; }
    public int getFbId() { return fbId; }
    public int getFbFormat() { return fbFormat; }
}
