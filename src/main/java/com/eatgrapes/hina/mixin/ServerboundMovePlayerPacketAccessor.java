package com.eatgrapes.hina.mixin;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 13:37
 */
@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
    @Accessor("onGround")
    void setOnGround(boolean onGround);
}
