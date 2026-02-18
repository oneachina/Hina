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

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import io.netty.channel.ChannelFutureListener;
import org.spongepowered.asm.mixin.Mixin;
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
