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
package com.hinaclient.hina.module.impl.combat;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.AttackEvent;
import com.hinaclient.hina.event.impl.ClientTickEvent;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class KnockbackRedirect extends Module {
    private final NumberSetting yawAngle = new NumberSetting("Yaw Angle", 0.0, -180.0, 180.0, 1.0);

    private Entity pendingTarget = null;
    private float originalYaw;
    private int tickCounter = 0;

    public KnockbackRedirect() {
        super("KnockbackRedirect", Category.COMBAT);
        addSetting(yawAngle);
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        if (client.player == null || tickCounter > 0) return;

        event.setCancelled(true);

        pendingTarget = event.getTarget();
        originalYaw = client.player.getYRot();

        client.player.setYRot(yawAngle.getValue().floatValue());
        tickCounter = 1;
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (tickCounter <= 0 || pendingTarget == null) return;

        if (tickCounter == 1) {
            if (pendingTarget.isAlive() && client.player.distanceTo(pendingTarget) <= 3.0) {
                client.gameMode.attack(client.player, pendingTarget);
                client.player.swing(InteractionHand.MAIN_HAND);
            }

            client.player.setYRot(originalYaw);

            pendingTarget = null;
            tickCounter = 0;
        }
    }
}