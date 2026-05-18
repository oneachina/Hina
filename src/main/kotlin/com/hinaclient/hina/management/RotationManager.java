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
package com.hinaclient.hina.management;

import com.hinaclient.hina.event.EventBus;
import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.ClientTickEvent;
import com.hinaclient.hina.event.impl.MoveInputEvent;
import com.hinaclient.hina.event.impl.packet.PacketEvent;
import com.hinaclient.hina.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;

public class RotationManager {
    public static final RotationManager INSTANCE = new RotationManager();
    private final Minecraft mc = Minecraft.getInstance();

    private float serverYaw, serverPitch;
    private float lastServerYaw, lastServerPitch;
    private int keepTicks = 0;

    public void init() {
        EventBus.INSTANCE.register(this);
    }

    public void apply(float targetYaw, float targetPitch, int ticks) {
        if (mc.player == null) return;

        float[] fixed = RotationUtils.applyGCD(targetYaw, targetPitch, lastServerYaw, lastServerPitch);
        float fixedYaw = fixed[0];
        float fixedPitch = fixed[1];

        this.serverYaw = fixedYaw;
        this.serverPitch = fixedPitch;
        this.keepTicks = Math.max(1, ticks);
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;
        if (keepTicks > 0) {
            keepTicks--;
        } else {
            serverYaw = mc.player.getYRot();
            serverPitch = mc.player.getXRot();
            lastServerYaw = serverYaw;
            lastServerPitch = serverPitch;
        }
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (mc.player == null || keepTicks <= 0) return;

        float forward = event.getForward();
        float strafe = event.getStrafe();

        double angle = Math.atan2(strafe, forward);
        double magnitude = Math.sqrt(forward * forward + strafe * strafe);

        if (magnitude == 0) return;

        double newAngle = angle + Math.toRadians(Mth.wrapDegrees(mc.player.getYRot() - serverYaw));

        event.setForward((float) (Math.cos(newAngle) * magnitude));
        event.setStrafe((float) (Math.sin(newAngle) * magnitude));
    }

    @EventListener
    public void onPacketSend(PacketEvent event) {
        if (mc.player == null || keepTicks <= 0) return;

        if (event.getPacket() instanceof ServerboundMovePlayerPacket movePacket) {
            boolean hasPos = movePacket.hasPosition();
            double x = movePacket.getX(mc.player.getX());
            double y = movePacket.getY(mc.player.getY());
            double z = movePacket.getZ(mc.player.getZ());

            if (hasPos) {
                event.setPacket(new ServerboundMovePlayerPacket.PosRot(x, y, z,
                        serverYaw, serverPitch,
                        movePacket.isOnGround(), movePacket.horizontalCollision()));
            } else {
                event.setPacket(new ServerboundMovePlayerPacket.Rot(serverYaw, serverPitch,
                        movePacket.isOnGround(), movePacket.horizontalCollision()));
            }
            lastServerYaw = serverYaw;
            lastServerPitch = serverPitch;
        }
    }

    public boolean isRotating() { return keepTicks > 0; }
    public float getServerYaw() { return serverYaw; }
    public float getServerPitch() { return serverPitch; }
}
