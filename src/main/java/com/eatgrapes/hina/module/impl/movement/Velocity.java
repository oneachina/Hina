package com.eatgrapes.hina.module.impl.movement;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.packet.PacketEvent;
import com.eatgrapes.hina.event.impl.packet.PacketType;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.BooleanSetting;
import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.setting.NumberSetting;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.PositionMoveRotation;

import java.util.Objects;
import java.util.Random;

public class Velocity extends Module {
    private final ModeSetting mode = new ModeSetting("mode", "Grim Water", "Grim Water");

    private final BooleanSetting fake = new BooleanSetting("fake", true);
    private final BooleanSetting velo = new BooleanSetting("velo", true);

    private final Random rand = new Random();

    public Velocity() {
        super("Velocity", Category.MOVEMENT);

        fake.setVisibility(() -> Objects.equals(mode.getValue(), "Grim"));
        velo.setVisibility(() -> Objects.equals(mode.getValue(), "Grim"));

        addSetting(mode);
        addSetting(fake);
        addSetting(velo);
    }

    @EventListener
    public void onPacket(PacketEvent e) {
        if (client.player == null || e.isCancelled()) return;
        if (e.getType() == PacketType.Receive) {
            if (velo.getValue() && e.getPacket() instanceof ClientboundSetEntityMotionPacket vel) {
                if (vel.getId() == client.player.getId()) {
                    e.setCancelled(true);
                }
            }

            if (fake.getValue() && e.getPacket() instanceof ClientboundPlayerPositionPacket s08) {
                var pos = s08.change();

                client.player.setPos(pos.position().x, pos.position().y, pos.position().z);
                client.getConnection().send(new ServerboundAcceptTeleportationPacket(s08.id()));
                e.setCancelled(true);
            }
        }
    }
}