package com.eatgrapes.hina.module.impl.movement;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.UpdateEvent;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.BooleanSetting;
import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.setting.NumberSetting;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 12:32
 */
public class Flight extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");

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

        vSpeed.setVisibility(() -> mode.getValue().equals("Vanilla"));
        vSprint.setVisibility(() -> mode.getValue().equals("Vanilla"));
        vSprintSpeed.setVisibility(() -> mode.getValue().equals("Vanilla") && vSprint.getValue());
    }

    @Override
    protected void onEnable() {
        if (client.player == null) return;
        if (mode.getValue().equals("Creative")) {
            client.player.getAbilities().mayfly = true;
            client.player.getAbilities().flying = true;
        }
    }

    @Override
    protected void onDisable() {
        if (client.player == null) return;
        client.player.getAbilities().flying = false;
        if (!client.player.isCreative()) {
            client.player.getAbilities().mayfly = false;
        }
    }

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (client.player == null) return;
        handleVanilla();
    }

    private void handleVanilla() {
        assert client.player != null;
        client.player.getAbilities().flying = true;
        float speed = (vSprint.getValue() && client.options.keySprint.isDown()) ?
                vSprintSpeed.getValue().floatValue() : vSpeed.getValue().floatValue();
        client.player.getAbilities().setFlyingSpeed(speed);

        if (bypass.getValue() && client.player.tickCount % 40 == 0) {
            Vec3 currentMovement = client.player.getDeltaMovement();
            client.player.setDeltaMovement(currentMovement.x(), -0.04, currentMovement.z);
        }
    }
}