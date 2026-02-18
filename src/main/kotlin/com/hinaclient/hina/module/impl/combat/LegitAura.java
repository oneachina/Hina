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
import com.hinaclient.hina.setting.BooleanSetting;
import com.hinaclient.hina.setting.ModeSetting;
import com.hinaclient.hina.setting.NumberSetting;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LegitAura extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.15, 0.01, 1.0, 0.01);
    private final NumberSetting range = new NumberSetting("Range", 4.5, 3.0, 8.0, 0.1);
    private final NumberSetting fov = new NumberSetting("FOV", 90.0, 10.0, 360.0, 1.0);
    private final ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Mace");
    private final BooleanSetting autoAttack = new BooleanSetting("Attack", true);
    private final BooleanSetting shieldBreaker = new BooleanSetting("ShieldBreaker", true);

    private int maceStage = 0;

    public LegitAura() {
        super("LegitAura", Category.COMBAT);
        addSetting(speed);
        addSetting(range);
        addSetting(fov);
        addSetting(mode);
        addSetting(autoAttack);
        addSetting(shieldBreaker);
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (client.player == null || client.level == null) return;

        double r = range.getValue();
        AABB searchBox = client.player.getBoundingBox().inflate(r);
        List<LivingEntity> targets = new ArrayList<>();

        client.level.getEntities().get(searchBox, entity -> {
            if (entity instanceof LivingEntity living && living != client.player && living.isAlive()
                    && client.player.distanceTo(living) <= r && isInFov(living)) {
                targets.add(living);
            }
        });

        LivingEntity target = targets.stream()
                .min(Comparator.comparingDouble(e -> client.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            applyRotations(target);
            if (autoAttack.getValue()) handleAttack(target);
        } else {
            maceStage = 0;
        }
    }

    private void handleAttack(LivingEntity target) {
        if (client.player.distanceTo(target) > 3.0 || !isLookingAt(target)) return;

        if (shieldBreaker.getValue() && isBlockedByShield(target)) {
            int axeSlot = findAxeSlot();
            if (axeSlot != -1) {
                client.player.getInventory().setSelectedSlot(axeSlot);
                if (client.player.getAttackStrengthScale(0.0F) >= 1.0F) performAttack(target);
                return;
            }
        }

        if (mode.is("Mace")) {
            int swordSlot = findSwordSlot();
            int maceSlot = findMaceSlot();

            if (swordSlot != -1 && maceSlot != -1 && !client.player.onGround()) {
                if (maceStage == 0 && client.player.getAttackStrengthScale(0.0F) >= 1.0F) {
                    client.player.getInventory().setSelectedSlot(maceSlot);
                    maceStage = 1;
                } else if (maceStage == 1) {
                    performAttack(target);
                    client.player.getInventory().setSelectedSlot(swordSlot);
                    maceStage = 0;
                }
            } else {
                defaultAttack(target);
            }
        } else {
            defaultAttack(target);
        }
    }

    private void performAttack(LivingEntity target) {
        assert client.player != null;
        client.gameMode.attack(client.player, target);
        client.player.swing(InteractionHand.MAIN_HAND);
    }

    private void defaultAttack(LivingEntity target) {
        if (client.player.getAttackStrengthScale(0.0F) >= 1.0F) performAttack(target);
    }

    private boolean isBlockedByShield(LivingEntity target) {
        if (!target.isBlocking()) return false;
        Vec3 diff = client.player.position().subtract(target.position()).normalize();
        return target.getViewVector(1.0F).normalize().dot(diff) > 0.0;
    }

    private int findSwordSlot() {
        for (int i = 0; i < 9; i++) {
            var item = client.player.getInventory().getItem(i).getItem();
            if (item == Items.DIAMOND_SWORD || item == Items.IRON_SWORD || item == Items.NETHERITE_SWORD || item == Items.GOLDEN_SWORD || item == Items.WOODEN_SWORD) return i;
        }
        return -1;
    }

    private int findMaceSlot() {
        for (int i = 0; i < 9; i++) if (client.player.getInventory().getItem(i).is(Items.MACE)) return i;
        return -1;
    }

    private int findAxeSlot() {
        for (int i = 0; i < 9; i++) if (client.player.getInventory().getItem(i).getItem() instanceof AxeItem) return i;
        return -1;
    }

    private boolean isInFov(LivingEntity target) {
        Vec3 playerEye = client.player.getEyePosition();
        double dx = target.getX() - playerEye.x;
        double dz = target.getZ() - playerEye.z;
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        return Math.abs(Mth.wrapDegrees(yaw - client.player.getYRot())) <= fov.getValue() / 2.0;
    }

    private boolean isLookingAt(LivingEntity target) {
        Vec3 view = client.player.getViewVector(1.0F).normalize();
        Vec3 targetDir = new Vec3(target.getX() - client.player.getX(), target.getEyeY() - client.player.getEyeY(), target.getZ() - client.player.getZ()).normalize();
        return view.dot(targetDir) > 0.9;
    }

    private void applyRotations(LivingEntity target) {
        Vec3 targetEye = target.getEyePosition();
        Vec3 playerEye = client.player.getEyePosition();
        double dx = targetEye.x - playerEye.x, dy = targetEye.y - playerEye.y, dz = targetEye.z - playerEye.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        float tYaw = Mth.wrapDegrees((float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f);
        float tPitch = (float) -(Math.atan2(dy, distXZ) * 180.0 / Math.PI);
        client.player.setYRot(client.player.getYRot() + Mth.wrapDegrees(tYaw - client.player.getYRot()) * speed.getValue().floatValue());
        client.player.setXRot(Mth.clamp(client.player.getXRot() + (tPitch - client.player.getXRot()) * speed.getValue().floatValue(), -90.0f, 90.0f));
    }
}
