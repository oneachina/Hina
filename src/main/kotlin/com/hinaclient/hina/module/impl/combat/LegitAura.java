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
import com.hinaclient.hina.management.RotationManager;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.BooleanSetting;
import com.hinaclient.hina.setting.ModeSetting;
import com.hinaclient.hina.setting.NumberSetting;
import com.hinaclient.hina.utils.RotationUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LegitAura extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.15, 0.01, 1.0, 0.01);
    private final NumberSetting range = new NumberSetting("Range", 4.5, 3.0, 8.0, 0.1);
    private final NumberSetting fov = new NumberSetting("FOV", 90.0, 10.0, 360.0, 1.0);
    private final ModeSetting mode = new ModeSetting("Mode", "1.9+", "1.8", "1.9+");
    private final ModeSetting aimMode = new ModeSetting("AimMode", "Best", "HeadLock", "BodyLock", "BodySway", "Best");
    private final BooleanSetting autoAttack = new BooleanSetting("Attack", true);
    private final BooleanSetting shieldBreaker = new BooleanSetting("ShieldBreaker", true);
    private final BooleanSetting instantSwitch = new BooleanSetting("InstantSwitch", false);
    private final BooleanSetting ignoreWalls = new BooleanSetting("IgnoreWalls", false);
    private final BooleanSetting slient = new BooleanSetting("slient (danger)", false);
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", true);
    private final BooleanSetting targetMonsters = new BooleanSetting("Monsters", false);
    private final BooleanSetting targetAnimals = new BooleanSetting("Animals", false);
    private final BooleanSetting ignoreInvisible = new BooleanSetting("IgnoreInvisible", false);

    private int chestSwapCooldown = 0;
    private float targetYaw;
    private float targetPitch;
    private double swayPhase = 0.0;

    public LegitAura() {
        super("LegitAura", Category.COMBAT);
        addSetting(speed);
        addSetting(range);
        addSetting(fov);
        addSetting(mode);
        addSetting(aimMode);
        addSetting(autoAttack);
        addSetting(shieldBreaker);
        addSetting(instantSwitch);
        addSetting(ignoreWalls);
        addSetting(slient);
        addSetting(targetPlayers);
        addSetting(targetMonsters);
        addSetting(targetAnimals);
        addSetting(ignoreInvisible);
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        if (client.player == null || client.level == null) return;

        if (chestSwapCooldown > 0) chestSwapCooldown--;

        swayPhase += 0.1;

        double r = range.getValue();
        AABB searchBox = client.player.getBoundingBox().inflate(r);
        List<LivingEntity> targets = new ArrayList<>();

        client.level.getEntities().get(searchBox, entity -> {
            if (!(entity instanceof LivingEntity living) || living == client.player || !living.isAlive()) return;
            if (client.player.distanceTo(living) > r) return;
            if (!isInFov(living)) return;
            if (!ignoreInvisible.getValue() && living.isInvisible()) return;
            switch (living) {
                case Player ignored2 when !targetPlayers.getValue() -> {
                    return;
                }
                case Monster ignored1 when !targetMonsters.getValue() -> {
                    return;
                }
                case Animal ignored when !targetAnimals.getValue() -> {
                    return;
                }
                default -> {
                }
            }
            targets.add(living);
        });

        LivingEntity target = targets.stream()
                .min(Comparator.comparingDouble(e -> client.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            applyRotations(target);
            if (autoAttack.getValue()) handleAttack(target);
        } else if (!slient.getValue()) {
            targetYaw = client.player.getYRot();
            targetPitch = client.player.getXRot();
        }

        if (!slient.getValue()) {
            float[] smoothed = RotationUtils.smooth(
                client.player.getYRot(), client.player.getXRot(),
                targetYaw, targetPitch,
                speed.getValue().floatValue() * 10.0 // Scaled speed for smoothing
            );
            
            client.player.setYRot(smoothed[0]);
            client.player.setXRot(smoothed[1]);
            client.player.yRotO = smoothed[0];
            client.player.xRotO = smoothed[1];
        }
    }

    private void handleAttack(LivingEntity target) {
        if (client.player.distanceTo(target) > 3.0) return;
        if (!ignoreWalls.getValue() && !isLookingAt(target)) return;

        boolean isNewVersion = mode.is("1.9+");

        if (shieldBreaker.getValue() && isBlockedByShield(target)) {
            int axeSlot = findAxeSlot();
            if (axeSlot != -1) {
                client.player.getInventory().setSelectedSlot(axeSlot);
                if (!isNewVersion || client.player.getAttackStrengthScale(0.0F) >= 1.0F) {
                    performAttack(target);
                }
                return;
            }
        }

        if (isNewVersion) {
            int swordSlot = findSwordSlot();
            int maceSlot = findMaceSlot();

            if (swordSlot != -1 && maceSlot != -1) {
                boolean isFalling = client.player.getDeltaMovement().y < -0.1;
                double fallDist = client.player.fallDistance;
                boolean canUseMace = isFalling && fallDist >= 2.0f;

                if (canUseMace) {
                    if (isFlyingWithElytra() && chestSwapCooldown == 0) {
                        if (tryEquipChestplate()) {
                            chestSwapCooldown = 5;
                            return;
                        }
                    }

                    if (client.player.getInventory().getSelectedSlot() != maceSlot) {
                        client.player.getInventory().setSelectedSlot(maceSlot);
                        return;
                    }
                    if (instantSwitch.getValue() || client.player.getAttackStrengthScale(0.0F) >= 0.1F) {
                        performAttack(target);
                    }
                } else {
                    if (client.player.getInventory().getSelectedSlot() != swordSlot) {
                        client.player.getInventory().setSelectedSlot(swordSlot);
                        return;
                    }
                    defaultAttack(target);
                }
            } else {
                defaultAttack(target);
            }
        } else {
            performAttack(target);
        }
    }

    private boolean isFlyingWithElytra() {
        return client.player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && client.player.isFallFlying();
    }

    private boolean tryEquipChestplate() {
        int chestplateSlot = findChestplateSlot();
        if (chestplateSlot == -1) return false;

        int armorSlot = 37;
        int containerId = client.player.containerMenu.containerId;

        client.gameMode.handleInventoryMouseClick(containerId, armorSlot, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(containerId, chestplateSlot, 0, ClickType.PICKUP, client.player);

        return true;
    }

    private void performAttack(LivingEntity target) {
        assert client.player != null;
        client.gameMode.attack(client.player, target);
        client.player.swing(InteractionHand.MAIN_HAND);
    }

    private void defaultAttack(LivingEntity target) {
        performAttack(target);
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

    private int findChestplateSlot() {
        for (int i = 0; i < 9; i++) {
            var item = client.player.getInventory().getItem(i).getItem();
            if (item == Items.DIAMOND_CHESTPLATE || item == Items.NETHERITE_CHESTPLATE || item == Items.GOLDEN_CHESTPLATE || item == Items.IRON_CHESTPLATE) {
                return i;
            }
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
        float[] rots = RotationUtils.getRotationsToEntity(target);
        return Math.abs(Mth.wrapDegrees(rots[0] - client.player.getYRot())) <= fov.getValue() / 2.0;
    }

    private boolean isLookingAt(LivingEntity target) {
        Vec3 view = client.player.getViewVector(1.0F).normalize();
        Vec3 targetDir = new Vec3(target.getX() - client.player.getX(), target.getEyeY() - client.player.getEyeY(), target.getZ() - client.player.getZ()).normalize();
        return view.dot(targetDir) > 0.9;
    }

    private void applyRotations(LivingEntity target) {
        float[] rots;
        String aim = aimMode.getValue();

        if ("Best".equals(aim)) {
            RotationUtils.RotationData data = RotationUtils.getBestRotationToEntity(target, range.getValue());
            rots = data.rotation();
        } else {
            Vec3 targetPoint;
            if ("HeadLock".equals(aim)) {
                targetPoint = target.getEyePosition();
            } else if ("BodyLock".equals(aim)) {
                targetPoint = target.position().add(0, target.getBbHeight() / 2, 0);
            } else { // BodySway
                double centerY = target.getY() + target.getBbHeight() / 2.0;
                double baseY = centerY + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.02;
                double halfWidth = target.getBbWidth() / 2.0;
                double horizontalOffset = Math.sin(swayPhase) * halfWidth * 0.8;
                Vec3 toTarget = target.position().subtract(client.player.getEyePosition()).normalize();
                Vec3 right = new Vec3(-toTarget.z, 0, toTarget.x).normalize();
                Vec3 center = new Vec3(target.getX(), baseY, target.getZ());
                targetPoint = center.add(right.scale(horizontalOffset));
            }
            rots = RotationUtils.getRotations(client.player.getEyePosition(), targetPoint);
        }

        if (slient.getValue()) {
            float[] smoothed = RotationUtils.smooth(
                RotationManager.INSTANCE.getServerYaw(),
                RotationManager.INSTANCE.getServerPitch(),
                rots[0], rots[1],
                speed.getValue().floatValue() * 20.0 // Higher speed for silent packets
            );
            RotationManager.INSTANCE.apply(smoothed[0], smoothed[1], 2);
        } else {
            targetYaw = rots[0];
            targetPitch = rots[1];
        }
    }
}