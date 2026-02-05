package com.eatgrapes.hina.event.impl.packet;

import net.minecraft.network.protocol.Packet;
import com.eatgrapes.hina.event.Event;

public class PacketEvent extends Event {
    private Packet<?> packet;
    private PacketType type;

    public PacketEvent(Packet<?> packet, PacketType type) {
        this.packet = packet;
        this.type = type;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) { this.packet = packet; }

    public PacketType getType() {
        return type;
    }
}