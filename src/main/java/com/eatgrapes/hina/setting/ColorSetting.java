/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.setting;

import java.awt.Color;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, int color) {
        super(name, color);
    }
    
    public ColorSetting(String name, Color color) {
        super(name, color.getRGB());
    }
    
    public int getColor() {
        return getValue();
    }
}