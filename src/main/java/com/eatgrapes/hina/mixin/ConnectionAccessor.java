package com.eatgrapes.hina.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import io.netty.channel.ChannelFutureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.jspecify.annotations.Nullable;

/**
 * @Author: oneachina
 * @Date: 2026/2/5 13:22
 */
@Mixin(Connection.class)
public interface ConnectionAccessor {
    @Invoker("sendPacket")
    void invokeSendPacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush);

    @Invoker("genericsFtw")
    static <T extends PacketListener> void genericsFtw(Packet<T> packet, PacketListener listener) {
        throw new UnsupportedOperationException();
    }
}
