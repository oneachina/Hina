/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina;

import com.eatgrapes.hina.event.fabric.HinaHandler;
import com.eatgrapes.hina.management.config.ConfigManager;
import com.eatgrapes.hina.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
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
                "category.hina.main"
        ));
        HinaHandler.init();
        HinaHandler.initEvent();
    }
}