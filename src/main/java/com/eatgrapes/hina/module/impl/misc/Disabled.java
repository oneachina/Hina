/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package com.eatgrapes.hina.module.impl.misc;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.ClientTickEvent;
import com.eatgrapes.hina.event.impl.packet.PacketEvent;
import com.eatgrapes.hina.event.impl.packet.PacketType;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.utils.chat.ChatUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.PositionMoveRotation;

public class Disabled extends Module {
    private final ModeSetting mode = new ModeSetting("mode", "Grim S08", "Grim S08", "BadPacketsX", "BadPacketsN");
    private boolean sentActionThisTick = false;
    private int lastTeleportId = -1;
    private boolean awaitingTeleport = false;

    public Disabled() {
        super("Disabled", Category.MISC);
        addSetting(mode);
        mode.setMulti(true);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        sentActionThisTick = false;
        awaitingTeleport = false;
        lastTeleportId = -1;
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (mode.is("Grim S08") && event.getType() == PacketType.Receive && event.getPacket() instanceof ClientboundPlayerPositionPacket(
                int id, PositionMoveRotation change, java.util.Set<net.minecraft.world.entity.Relative> relatives
        )) {
            if (client.player != null && client.getConnection() != null) {
                PositionMoveRotation currentLocal = PositionMoveRotation.of(client.player);
                PositionMoveRotation absolute = PositionMoveRotation.calculateAbsolute(currentLocal, change, relatives);

                client.player.setPos(absolute.position().x, absolute.position().y, absolute.position().z);
                client.getConnection().send(new ServerboundAcceptTeleportationPacket(id));

                event.setCancelled(true);
            }
        }

        if (mode.is("BadPacketsN")) {
            if (event.getType() == PacketType.Receive && event.getPacket() instanceof ClientboundPlayerPositionPacket s08) {
                lastTeleportId = s08.id();
                awaitingTeleport = true;
                ChatUtils.debug("BadPacketsN", "Locked. Waiting for Teleport ID: " + lastTeleportId);
            }

            if (event.getType() == PacketType.Send) {
                Packet<?> packet = event.getPacket();

                if (awaitingTeleport && packet instanceof ServerboundMovePlayerPacket) {
                    event.setCancelled(true);
                    ChatUtils.debug("BadPacketsN", "Cancelled movement packet before teleport confirmation.");
                }

                if (packet instanceof ServerboundAcceptTeleportationPacket confirm) {
                    if (confirm.getId() == lastTeleportId) {
                        awaitingTeleport = false;
                        ChatUtils.debug("BadPacketsN", "Teleport confirmed (ID: " + lastTeleportId + "). Unlocking movement.");
                    }
                }
            }
        }

        if (mode.is("BadPacketsX") && event.getType() == PacketType.Send && event.getPacket() instanceof ServerboundPlayerCommandPacket c0b) {
            ServerboundPlayerCommandPacket.Action action = c0b.getAction();
            if (action == ServerboundPlayerCommandPacket.Action.START_SPRINTING || action == ServerboundPlayerCommandPacket.Action.STOP_SPRINTING) {
                if (sentActionThisTick) {
                    ChatUtils.debug("BadPacketsX", "Cancelled duplicate C0B (Action: " + action.name() + ")");
                    event.setCancelled(true);
                } else {
                    sentActionThisTick = true;
                }
            }
        }
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        sentActionThisTick = false;
    }
}