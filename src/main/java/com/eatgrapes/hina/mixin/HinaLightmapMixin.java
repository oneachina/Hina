/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.eatgrapes.hina.HinaClient;
import com.eatgrapes.hina.module.impl.render.FullbrightModule;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class HinaLightmapMixin {
    @ModifyExpressionValue(method = "update(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 1))
    private Object injectFullBright(Object original) {
        FullbrightModule fullbright = (FullbrightModule) HinaClient.INSTANCE.moduleManager.getModuleByName("Fullbright");
        if (fullbright != null && fullbright.isEnabled()) {
            return fullbright.getGamma().getValue();
        }
        return original;
    }
}