package com.hinaclient.hina.module.impl.combat;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.AttackEvent;
import com.hinaclient.hina.event.impl.ClientTickEvent;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Random;

public class MacePlus extends Module {
    private static final Random RANDOM = new Random();

    private int originalSlot = -1;
    private long revertTime = -1;
    private boolean pendingRevert = false;

    private boolean shieldBreakInProgress = false;
    private int maceSlot = -1;
    private LivingEntity shieldTarget;

    public MacePlus() {
        super("MacePlus", Category.COMBAT);
    }

    @EventListener
    private void onAttack(AttackEvent event) {
        if (client.player == null || client.level == null) return;
        LivingEntity target = event.getTarget();
        if (target == null) return;

        if (hasShield(target)) {
            int axeSlot = findAxeSlot();
            maceSlot = findBestMaceSlot();
            if (axeSlot == -1 || maceSlot == -1) return;

            originalSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(axeSlot);
            shieldTarget = target;
            shieldBreakInProgress = true;
            return;
        }

        int currentSlot = client.player.getInventory().getSelectedSlot();
        int bestMace = findBestMaceSlot();
        if (bestMace == -1) return;

        if (currentSlot == bestMace) {
            if (pendingRevert) {
                revertTime = System.currentTimeMillis() + randomRevertDelay();
            }
            return;
        }

        originalSlot = currentSlot;
        client.player.getInventory().setSelectedSlot(bestMace);
        revertTime = System.currentTimeMillis() + randomRevertDelay();
        pendingRevert = true;
    }

    @EventListener
    private void onTick(ClientTickEvent event) {
        if (client.player == null || client.level == null) return;
        long now = System.currentTimeMillis();

        if (shieldBreakInProgress) {
            if (shieldTarget == null || shieldTarget.isRemoved()) {
                revertAfterShieldBreak();
                return;
            }
            if (!shieldTarget.isBlocking()) {
                client.player.getInventory().setSelectedSlot(maceSlot);
                KeyMapping.click(client.options.keyAttack.getDefaultKey());
                revertAfterShieldBreak();
            }
            return;
        }

        if (pendingRevert && now >= revertTime) {
            client.player.getInventory().setSelectedSlot(originalSlot);
            originalSlot = -1;
            pendingRevert = false;
        }
    }

    private void revertAfterShieldBreak() {
        shieldBreakInProgress = false;
        maceSlot = -1;
        shieldTarget = null;
        if (client.player != null && originalSlot != -1) {
            client.player.getInventory().setSelectedSlot(originalSlot);
        }
        originalSlot = -1;
    }

    private boolean hasShield(LivingEntity entity) {
        if (entity.isBlocking()) return true;
        return entity.getMainHandItem().getItem() == Items.SHIELD
            || entity.getOffhandItem().getItem() == Items.SHIELD;
    }

    private int findAxeSlot() {
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = client.player.getInventory().getItem(i);
            if (stack.getItem() instanceof AxeItem) return i;
        }
        return -1;
    }

    private int findBestMaceSlot() {
        if (client.player == null) return -1;
        boolean canUseHeavyMace = isFallingForHeavyMace();
        int breach = -1, density = -1, any = -1;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = client.player.getInventory().getItem(i);
            if (stack.getItem() != Items.MACE) continue;
            any = i;
            if (hasEnchant(stack, Enchantments.BREACH)) breach = i;
            if (hasEnchant(stack, Enchantments.DENSITY)) density = i;
        }
        if (canUseHeavyMace) {
            if (density != -1) return density;
            if (any != -1) return any;
            return breach;
        } else {
            if (breach != -1) return breach;
            if (any != -1) return any;
            return density;
        }
    }

    private boolean isFallingForHeavyMace() {
        if (client.player == null) return false;
        return client.player.getDeltaMovement().y < -0.1 && client.player.fallDistance >= 2.0;
    }

    private boolean hasEnchant(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) return false;
        Holder<Enchantment> holder = getEnchantmentsHolder(key);
        if (holder == null) return false;
        return EnchantmentHelper.getItemEnchantmentLevel(holder, stack) > 0;
    }

    private Holder<Enchantment> getEnchantmentsHolder(ResourceKey<Enchantment> key) {
        if (client.level == null) return null;
        var registry = client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return registry.get(key).orElse(null);
    }

    private int randomRevertDelay() {
        return 86 + RANDOM.nextInt(47);
    }

    @Override
    protected void onDisable() {
        if (shieldBreakInProgress) {
            revertAfterShieldBreak();
        } else if (pendingRevert && client.player != null && originalSlot != -1) {
            client.player.getInventory().setSelectedSlot(originalSlot);
        }
        originalSlot = -1;
        pendingRevert = false;
        super.onDisable();
    }
}
