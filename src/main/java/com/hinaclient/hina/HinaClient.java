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

package com.hinaclient.hina;

import com.hinaclient.hina.event.fabric.HinaHandler;
import com.hinaclient.hina.management.config.ConfigManager;
import com.hinaclient.hina.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class HinaClient implements ClientModInitializer {
    public static final String MOD_ID = "hinaclient";
    public static HinaClient INSTANCE;
    public ModuleManager moduleManager;
    public ConfigManager configManager;
    public KeyMapping clickGuiKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        moduleManager = new ModuleManager();
        moduleManager.init();
        configManager = new ConfigManager();
        configManager.load();
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.hina.clickgui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hina", "keymapping/clickgui"))
        ));
        HinaHandler.init();
        HinaHandler.initEvent();
    }
}