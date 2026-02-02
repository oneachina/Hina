/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package com.eatgrapes.hina.event.impl;
import net.minecraft.network.protocol.Packet;

import com.eatgrapes.hina.event.Event;

public class PacketEvent extends Event {
    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }
}