/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module.impl.render;

import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.NumberSetting;

public class FullbrightModule extends Module {
    private final NumberSetting Gamma = new NumberSetting("Gamma", 4.0, 0.0, 4.0, 0.1);

    public FullbrightModule() {
        super("Fullbright", Category.RENDER);

        addSetting(Gamma);
    }

    public NumberSetting getGamma() {
        return Gamma;
    }
}