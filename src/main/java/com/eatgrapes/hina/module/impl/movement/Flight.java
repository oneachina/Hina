package com.eatgrapes.hina.module.impl.movement;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.UpdateEvent;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.BooleanSetting;
import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 12:32
 */
public class Flight extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final ModeSetting mode = new ModeSetting("Mode", "Creative", "Creative", "Vanilla");

    private final NumberSetting vSpeed = new NumberSetting("V-Speed", 0.05, 0.01, 1.0, 0.01);
    private final BooleanSetting vSprint = new BooleanSetting("V-Sprint", true);
    private final NumberSetting vSprintSpeed = new NumberSetting("V-SprintSpeed", 0.1, 0.01, 1.0, 0.01);

    private final BooleanSetting bypass = new BooleanSetting("Bypass", true);

    public Flight() {
        super("Flight", Category.MOVEMENT);
        this.setKey(GLFW.GLFW_KEY_G);

        addSetting(mode);
        addSetting(vSpeed);
        addSetting(vSprint);
        addSetting(vSprintSpeed);
        addSetting(bypass);

        vSpeed.setVisibility(() -> mode.getValue().equals("Creative"));
        vSprint.setVisibility(() -> mode.getValue().equals("Creative"));
        vSprintSpeed.setVisibility(() -> mode.getValue().equals("Creative") && vSprint.getValue());
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        if (mode.getValue().equals("Creative")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.getAbilities().flying = false;
        if (!mc.player.isCreative()) {
            mc.player.getAbilities().allowFlying = false;
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        handleVanila();
    }

    private void handleVanila() {
        mc.player.getAbilities().flying = true;
        float speed = (vSprint.getValue() && mc.options.sprintKey.isPressed()) ?
                vSprintSpeed.getValue().floatValue() : vSpeed.getValue().floatValue();
        mc.player.getAbilities().setFlySpeed(speed);

        if (bypass.getValue() && mc.player.age % 40 == 0) {
            mc.player.setVelocity(mc.player.getVelocity().x, -0.04, mc.player.getVelocity().z);
        }
    }
}