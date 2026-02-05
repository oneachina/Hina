package com.eatgrapes.hina.module.impl.player;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.packet.PacketEvent;
import com.eatgrapes.hina.event.impl.packet.PacketType;
import com.eatgrapes.hina.mixin.ServerboundMovePlayerPacketAccessor;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.ModeSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 16:49
 */
public class NoFall extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "No Ground", "No Ground");

    public NoFall() {
        super("NoFall", Category.PLAYER);
        addSetting(mode);
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (client.player == null) return;

        if (event.getType() == PacketType.Send && mode.getValue().equals("No Ground") && event.getPacket() instanceof ServerboundMovePlayerPacket packet) {
            ((ServerboundMovePlayerPacketAccessor) packet).setOnGround(false);
        }
    }
}
