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

package com.hinaclient.hina.module.impl.movement;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.packet.PacketEvent;
import com.hinaclient.hina.event.impl.packet.PacketType;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.BooleanSetting;
import com.hinaclient.hina.setting.ModeSetting;
import net.minecraft.network.protocol.game.*;

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