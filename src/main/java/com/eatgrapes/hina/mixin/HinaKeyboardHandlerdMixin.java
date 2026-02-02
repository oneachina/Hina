package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.mod.KeyEvent;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 12:39
 */
@Mixin(KeyboardHandler.class)
public class HinaKeyboardHandlerdMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void keyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == 1) {
            EventBus.INSTANCE.post(new KeyEvent(key));
        }
    }
}
