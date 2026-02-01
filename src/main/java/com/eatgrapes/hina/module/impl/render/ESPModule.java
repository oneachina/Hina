/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module.impl.render;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.Render3DEvent;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.BooleanSetting;
import com.eatgrapes.hina.setting.ColorSetting;
import com.eatgrapes.hina.setting.ModeSetting;
import com.eatgrapes.hina.setting.NumberSetting;
import com.eatgrapes.hina.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;

public class ESPModule extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final ColorSetting playerColor = new ColorSetting("Player Color", new Color(255, 255, 255));
    private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
    private final ColorSetting mobColor = new ColorSetting("Mob Color", Color.RED);
    private final BooleanSetting animals = new BooleanSetting("Animals", false);
    private final ColorSetting animalColor = new ColorSetting("Animal Color", Color.GREEN);

    private final BooleanSetting unifiedColor = new BooleanSetting("Unified Color", false);
    private final ColorSetting commonColor = new ColorSetting("Common Color", Color.CYAN);

    private final ModeSetting mode = new ModeSetting("Mode", "Box", "Box", "2D");
    private final NumberSetting thickness = new NumberSetting("Thickness", 2.0, 1.0, 5.0, 0.1);

    public ESPModule() {
        super("ESP", Category.RENDER);

        playerColor.setVisibility(() -> !unifiedColor.getValue() && players.getValue());
        mobColor.setVisibility(() -> !unifiedColor.getValue() && mobs.getValue());
        animalColor.setVisibility(() -> !unifiedColor.getValue() && animals.getValue());
        commonColor.setVisibility(unifiedColor::getValue);

        addSetting(players);
        addSetting(playerColor);
        addSetting(mobs);
        addSetting(mobColor);
        addSetting(animals);
        addSetting(animalColor);
        addSetting(unifiedColor);
        addSetting(commonColor);
        addSetting(mode);
        addSetting(thickness);
    }

    @EventListener
    private void onRender3D(Render3DEvent event) {
        if (!isEnabled()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || event.getTickCounter() == null) return;

        float delta = event.getTickCounter().getTickDelta(false);
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        VertexConsumer vc = event.getVertexConsumers().getBuffer(RenderUtils.LINES_NO_DEPTH);
        MatrixStack stack = event.getMatrixStack();
        float lineW = thickness.getValue().floatValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || entity.isRemoved()) continue;

            int colorInt = getColorInt(entity);

            if (colorInt == -1) continue;

            float r = ((colorInt >> 16) & 0xFF) / 255f;
            float g = ((colorInt >> 8) & 0xFF) / 255f;
            float b = (colorInt & 0xFF) / 255f;
            float a = ((colorInt >> 24) & 0xFF) / 255f;

            double x = entity.prevX + (entity.getX() - entity.prevX) * delta - camPos.x;
            double y = entity.prevY + (entity.getY() - entity.prevY) * delta - camPos.y;
            double z = entity.prevZ + (entity.getZ() - entity.prevZ) * delta - camPos.z;

            Box eb = entity.getBoundingBox();
            double w = (eb.maxX - eb.minX) / 2.0;
            double h = eb.maxY - eb.minY;
            double d = (eb.maxZ - eb.minZ) / 2.0;

            stack.push();
            stack.translate(x, y, z);
            RenderUtils.drawBox(stack, vc, -w, 0, -d, w, h, d, r, g, b, a, lineW);
            stack.pop();
        }
    }

    private int getColorInt(Entity entity) {
        int colorInt = -1;
        if (entity instanceof PlayerEntity && players.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : playerColor.getColor();
        } else if (entity instanceof Monster && mobs.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : mobColor.getColor();
        } else if (entity instanceof AnimalEntity && animals.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : animalColor.getColor();
        }
        return colorInt;
    }
}