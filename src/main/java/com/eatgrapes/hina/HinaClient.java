/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina;

import com.eatgrapes.hina.event.fabric.HinaHandler;
import com.eatgrapes.hina.management.config.ConfigManager;
import com.eatgrapes.hina.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HinaClient implements ClientModInitializer {
    public static final String MOD_ID = "hinaclient";
    public static HinaClient INSTANCE;
    public ModuleManager moduleManager;
    public ConfigManager configManager;
    public KeyBinding clickGuiKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        moduleManager = new ModuleManager();
        moduleManager.init();
        configManager = new ConfigManager();
        configManager.load();
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hina.clickgui", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.hina.main" 
        ));
        HinaHandler.init();
        HinaHandler.initEvent();
    }
}