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
package com.hinaclient.hina.ui;

import com.hinaclient.hina.HinaClient;
import com.hinaclient.hina.event.EventBus;
import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.skia.EventSkiaDrawScene;
import com.hinaclient.hina.module.Module;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class EditScreen extends Screen {
    private final List<Module> draggableModules;
    private Module draggingModule = null;
    private double dragOffsetX, dragOffsetY;

    public EditScreen() {
        super(Component.literal("EditScreen"));
        this.draggableModules = HinaClient.getINSTANCE().moduleManager.getModules().stream()
                .filter(this::hasSkiaRender)
                .collect(Collectors.toList());
    }

    private boolean hasSkiaRender(Module module) {
        for (Method method : module.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventListener.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && params[0].getCanonicalName().contains("EventSkiaDrawScene")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void init() {
        EventBus.INSTANCE.register(this);
    }

    @EventListener
    public void onSkiaRender(@NotNull EventSkiaDrawScene event) {
        var canvas = event.getCanvas();
        float sw = (float) this.minecraft.getWindow().getGuiScaledWidth();
        float sh = (float) this.minecraft.getWindow().getGuiScaledHeight();
        int scale = this.minecraft.getWindow().getGuiScale();

        try (var p = new Paint().setColor(0x40000000)) {
            canvas.drawRect(Rect.makeWH(sw * scale, sh * scale), p);
        }

        if (draggingModule != null) {
            double mouseX = this.minecraft.mouseHandler.xpos() * sw / this.minecraft.getWindow().getScreenWidth();
            double mouseY = this.minecraft.mouseHandler.ypos() * sh / this.minecraft.getWindow().getScreenHeight();
            draggingModule.setX(mouseX - dragOffsetX);
            draggingModule.setY(mouseY - dragOffsetY);
        }

        canvas.save();
        canvas.scale(scale, scale);
        try (var p = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(1f)) {
            for (Module mod : draggableModules) {
                p.setColor(mod == draggingModule ? 0xFFD1C4E9 : 0x80FFFFFF);
                canvas.drawRRect(RRect.makeXYWH((float)mod.getX(), (float)mod.getY(), 100, 30, 4), p);
            }
        }
        canvas.restore();
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean bl) {
        float sw = (float) this.minecraft.getWindow().getGuiScaledWidth();
        float sh = (float) this.minecraft.getWindow().getGuiScaledHeight();
        double mouseX = this.minecraft.mouseHandler.xpos() * sw / this.minecraft.getWindow().getScreenWidth();
        double mouseY = this.minecraft.mouseHandler.ypos() * sh / this.minecraft.getWindow().getScreenHeight();

        for (Module mod : draggableModules) {
            if (mouseX >= mod.getX() && mouseX <= mod.getX() + 100 && mouseY >= mod.getY() && mouseY <= mod.getY() + 30) {
                draggingModule = mod;
                dragOffsetX = mouseX - mod.getX();
                dragOffsetY = mouseY - mod.getY();
                return true;
            }
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        if (event.button() == 0) {
            draggingModule = null;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void removed() {
        EventBus.INSTANCE.unregister(this);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
