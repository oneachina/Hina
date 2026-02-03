package com.eatgrapes.hina.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 22:38
 */
@Mixin(targets = "com.mojang.blaze3d.opengl.GlStateManager$BlendState")
public interface BlendStateAccessor {
    @Accessor("srcRgb") @Mutable void setSrcRgb(int val);
    @Accessor("dstRgb") @Mutable void setDstRgb(int val);
    @Accessor("srcAlpha") @Mutable void setSrcAlpha(int val);
    @Accessor("dstAlpha") @Mutable void setDstAlpha(int val);
}
