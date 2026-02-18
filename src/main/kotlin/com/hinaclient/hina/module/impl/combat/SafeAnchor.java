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
import com.hinaclient.hina.event.impl.ClientTickEvent;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SafeAnchor extends Module {
    private int oldSlot = -1;
    private long lastActionTime = 0L;
    private BlockHitResult lastBhr = null;

    public SafeAnchor() {
        super("SafeAnchor", Category.COMBAT);
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (client.player == null || client.level == null || client.gameMode == null) return;
        if (oldSlot != -1 && Util.getMillis() - lastActionTime > 70L) {
            client.player.getInventory().setSelectedSlot(oldSlot);
            if (lastBhr != null) {
                client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, lastBhr);
                client.player.swing(InteractionHand.MAIN_HAND);
            }

            oldSlot = -1;
            lastBhr = null;
            return;
        }

        if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) client.hitResult;
            BlockState state = client.level.getBlockState(bhr.getBlockPos());

            if (state.is(Blocks.RESPAWN_ANCHOR)) {
                int currentCharges = state.getValue(RespawnAnchorBlock.CHARGE);

                if (client.options.keyUse.isDown() && currentCharges < 4) {
                    if (Util.getMillis() - lastActionTime > 50L) {
                        change(bhr);
                    }
                }
            }
        }
    }

    private void change(BlockHitResult bhr) {
        int glowstoneSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(Items.GLOWSTONE)) {
                glowstoneSlot = i;
                break;
            }
        }

        if (glowstoneSlot != -1) {
            oldSlot = client.player.getInventory().getSelectedSlot();
            lastBhr = bhr;

            client.player.getInventory().setSelectedSlot(glowstoneSlot);

            client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, bhr);
            client.player.swing(InteractionHand.MAIN_HAND);

            lastActionTime = Util.getMillis();
        }
    }
}