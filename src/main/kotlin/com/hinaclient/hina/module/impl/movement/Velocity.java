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
import com.hinaclient.hina.event.impl.ClientTickEvent;
import com.hinaclient.hina.event.impl.MoveEvent;
import com.hinaclient.hina.event.impl.UpdateEvent;
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
    private final ModeSetting mode = new ModeSetting("mode", "GrimS12", "GrimS12");
    private boolean enb;
    private int onGroundTicks;

    public Velocity() {
        super("Velocity", Category.MOVEMENT);

        addSetting(mode);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        enb = false;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        enb = false;
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (client.player != null) {
            if (client.player.onGround()) {
                onGroundTicks++;
            } else {
                onGroundTicks = 0;
            }
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (client.player == null || client.level == null) return;

        if (client.player.onGround() && onGroundTicks > 5 && mode.getValue().equals("GrimS12")) {
            enb = true;
        }
    }

    @EventListener
    public void onPacket(PacketEvent e) {
        if (client.player == null || e.isCancelled() || client.getConnection() == null) return;
        if (enb && mode.getValue().equals("GrimS12")) {
            if (e.getPacket() instanceof ClientboundSetEntityMotionPacket s12 && s12.getId() == client.player.getId()) {
                e.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onMove(MoveEvent e) {
        if (enb && client.player != null) e.setCancelled(true);
    }
}