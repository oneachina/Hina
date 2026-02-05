/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package com.eatgrapes.hina.mixin;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.packet.PacketEvent;
import com.eatgrapes.hina.event.impl.packet.PacketType;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Connection.class)
public class HinaConnectionMixin {
    @Redirect(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;sendPacket(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V")
    )
    private void redirectSendPacket(Connection instance, Packet<?> packet, ChannelFutureListener listener, boolean flush) {
        PacketEvent event = new PacketEvent(packet, PacketType.Send);
        EventBus.INSTANCE.post(event);

        if (!event.isCancelled()) {
            ((ConnectionAccessor)instance).invokeSendPacket(event.getPacket(), listener, flush);
        }
    }

    @Redirect(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V")
    )
    private void redirectGenericsFtw(Packet<?> packet, PacketListener listener) {
        PacketEvent event = new PacketEvent(packet, PacketType.Receive);
        EventBus.INSTANCE.post(event);

        if (!event.isCancelled()) {
            ConnectionAccessor.genericsFtw(event.getPacket(), listener);
        }
    }
}