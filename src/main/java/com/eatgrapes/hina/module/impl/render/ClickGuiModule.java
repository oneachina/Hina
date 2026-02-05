/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module.impl.render;

import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.ui.ClickGuiScreen;
import com.eatgrapes.hina.setting.*;

public class ClickGuiModule extends Module {
    public static ColorSetting themeColor = new ColorSetting("Theme Color", 0xFF4CAF50);
    public static ModeSetting test4 = new ModeSetting("Test 4", "a", "a", "b", "c");
    public static BooleanSetting debug = new BooleanSetting("debug", false);

    public ClickGuiModule() {
        super("ClickGUI", Category.RENDER);
        addSetting(new BooleanSetting("Test 1", true));
        addSetting(new NumberSetting("Test 2", 1.0, 0.1, 5.0, 0.1));
        addSetting(new ModeSetting("Test 3", "Modern", "Modern", "Classic", "Minimal"));
        addSetting(test4);
        addSetting(themeColor);
        addSetting(debug);

        test4.setMulti(true);
    }
    
    public static int getThemeColor() {
        return themeColor.getValue();
    }

    @Override
    protected void onEnable() {
        if (client.screen == null) {
            client.execute(() -> {
                client.setScreen(new ClickGuiScreen(this));
            });
        }
    }
    
    @Override
    protected void onDisable() {
        if (client.screen instanceof ClickGuiScreen) client.setScreen(null);
    }
}
