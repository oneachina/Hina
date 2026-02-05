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

package com.hinaclient.hina.module.impl.render;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.Render3DEvent;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.BooleanSetting;
import com.hinaclient.hina.setting.ColorSetting;
import com.hinaclient.hina.setting.ModeSetting;
import com.hinaclient.hina.setting.NumberSetting;
import com.hinaclient.hina.utils.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;

/**
 * @author Eatgrapes, oneachina
 * @link github.com/Eatgrapes
 */
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
        if (client.level == null || client.player == null || event.getTickCounter() == null) return;

        float delta = event.getTickCounter().getGameTimeDeltaPartialTick(false);
        Vec3 camPos = client.gameRenderer.getMainCamera().position();
        VertexConsumer vc = event.getVertexConsumers().getBuffer(RenderUtils.QUADS_NO_DEPTH);
        PoseStack stack = event.getPoseStack();
        float lineW = thickness.getValue().floatValue();

        for (Entity entity : client.level.getEntities().getAll()) {
            if (entity == client.player || entity.isRemoved()) continue;

            int colorInt = getColorInt(entity);

            if (colorInt == -1) continue;

            float r = ((colorInt >> 16) & 0xFF) / 255f;
            float g = ((colorInt >> 8) & 0xFF) / 255f;
            float b = (colorInt & 0xFF) / 255f;
            float a = ((colorInt >> 24) & 0xFF) / 255f;

            double x = entity.xo + (entity.getX() - entity.xo) * delta - camPos.x;
            double y = entity.yo + (entity.getY() - entity.yo) * delta - camPos.y;
            double z = entity.zo + (entity.getZ() - entity.zo) * delta - camPos.z;

            AABB eb = entity.getBoundingBox();
            double w = (eb.maxX - eb.minX) / 2.0;
            double h = eb.maxY - eb.minY;
            double d = (eb.maxZ - eb.minZ) / 2.0;

            stack.pushPose();
            stack.translate(x, y, z);
            RenderUtils.drawBox(stack, vc, -w, 0, -d, w, h, d, r, g, b, a, lineW);
            stack.popPose();
        }
    }

    private int getColorInt(Entity entity) {
        int colorInt = -1;
        if (entity instanceof Player && players.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : playerColor.getColor();
        } else if (entity instanceof Monster && mobs.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : mobColor.getColor();
        } else if (entity instanceof Animal && animals.getValue()) {
            colorInt = unifiedColor.getValue() ? commonColor.getColor() : animalColor.getColor();
        }
        return colorInt;
    }
}