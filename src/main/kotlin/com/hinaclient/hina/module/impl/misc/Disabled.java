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

package com.hinaclient.hina.module.impl.misc;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.packet.PacketEvent;
import com.hinaclient.hina.event.impl.packet.PacketType;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.ModeSetting;
import com.hinaclient.hina.utils.chat.ChatUtils;
import com.hinaclient.hina.utils.world.WorldUtils;
import net.minecraft.network.protocol.game.*;
import java.util.ArrayList;
import java.util.List;

public class Disabled extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "GrimS08", "GrimS08", "BadPacketsN", "BadPacketsI", "GroundSpoof");
    private final List<Integer> teleportQueue = new ArrayList<>();

    public Disabled() {
        super("Disabler", Category.MISC);
        mode.setMulti(true);

        addSetting(mode);
    }

    @Override
    public void onDisable() {
        teleportQueue.clear();
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (client.player == null) return;

        if (event.getType() == PacketType.Receive) {
            if (mode.is("GrimS08") && event.getPacket() instanceof ClientboundPlayerPositionPacket s08) {
                teleportQueue.add(s08.id());
                ChatUtils.debug("GrimS08", "Queue Add: " + s08.id());
            }
        }

        if (event.getType() == PacketType.Send) {
            if (mode.is("BadPacketsN") && event.getPacket() instanceof ServerboundMovePlayerPacket) {
                if (!teleportQueue.isEmpty()) {
                    event.setCancelled(true);
                    ChatUtils.debug("BadPacketsN", "Blocking Move (Waiting for S08 Confirm)");
                    return;
                }
            }

            if (event.getPacket() instanceof ServerboundAcceptTeleportationPacket confirm) {
                teleportQueue.remove(Integer.valueOf(confirm.getId()));
                ChatUtils.debug("GrimS08", "Queue Remove: " + confirm.getId());
            }

            if (mode.is("GroundSpoof") && event.getPacket() instanceof ServerboundMovePlayerPacket packet) {
                if (client.player.fallDistance > 3.0f) {
                    double groundY = WorldUtils.getGroundY(client.player);
                    if (groundY != -999) {
                        double currentY = packet.getY(client.player.getY());
                        double diff = currentY - groundY;

                        if (diff > 0 && diff < 0.5) {
                            ServerboundMovePlayerPacket replaced = null;
                            if (packet.hasPosition() && packet.hasRotation()) {
                                replaced = new ServerboundMovePlayerPacket.PosRot(
                                        packet.getX(0), groundY, packet.getZ(0),
                                        packet.getYRot(0), packet.getXRot(0),
                                        true, packet.horizontalCollision()
                                );
                            } else if (packet.hasPosition()) {
                                replaced = new ServerboundMovePlayerPacket.Pos(
                                        packet.getX(0), groundY, packet.getZ(0),
                                        true, packet.horizontalCollision()
                                );
                            }

                            if (replaced != null) {
                                event.setPacket(replaced);
                                client.player.setPos(client.player.getX(), groundY, client.player.getZ());
                                client.player.setOnGround(true);
                                client.player.fallDistance = 0;
                                ChatUtils.debug("GroundSpoof", "Snapped to: " + groundY);
                            }
                        }
                    }
                }
            }

            if (mode.is("BadPacketsI") && event.getPacket() instanceof ServerboundPlayerAbilitiesPacket abilities) {
                if (!client.player.getAbilities().mayfly && abilities.isFlying()) {
                    event.setCancelled(true);
                    ChatUtils.debug("BadPacketsI", "Cancelled illegal flying packet");
                }
            }
        }
    }
}