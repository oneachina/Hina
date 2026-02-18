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

package com.hinaclient.hina.mixin;

import com.hinaclient.hina.event.EventBus;
import com.hinaclient.hina.event.impl.MoveEvent;
import com.hinaclient.hina.event.impl.UpdateEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 12:36
 */
@Mixin(LocalPlayer.class)
public abstract class HinaLocalPlayerMixin extends Entity {
    public HinaLocalPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(new UpdateEvent());
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void onMove(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        MoveEvent event = new MoveEvent(moverType, vec3);
        EventBus.INSTANCE.post(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (event.getVec() != vec3) {
            super.move(moverType, event.getVec());
            ci.cancel();
        }
    }
}
