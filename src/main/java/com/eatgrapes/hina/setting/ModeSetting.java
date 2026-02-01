/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String current, String... modes) {
        super(name, current);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
    }

    public void cycle() {
        index++;
        if (index >= modes.size()) index = 0;
        setValue(modes.get(index));
    }
    
    public List<String> getModes() {
        return modes;
    }
}