package com.eatgrapes.hina.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private final List<Boolean> activeStates;
    private boolean multi;
    private int index;

    public ModeSetting(String name, String current, String... modes) {
        super(name, current);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
        this.activeStates = new ArrayList<>();
        for (int i = 0; i < this.modes.size(); i++) {
            activeStates.add(i == index);
        }
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public boolean isMulti() {
        return multi;
    }

    public boolean is(String name) {
        int idx = modes.indexOf(name);
        if (idx == -1) return false;
        return multi ? activeStates.get(idx) : getValue().equalsIgnoreCase(name);
    }

    public void toggle(String name) {
        int idx = modes.indexOf(name);
        if (idx != -1) {
            activeStates.set(idx, !activeStates.get(idx));
            updateMultiValue();
        }
    }

    private void updateMultiValue() {
        if (!multi) return;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < modes.size(); i++) {
            if (activeStates.get(i)) {
                if (count > 0) sb.append(", ");
                sb.append(modes.get(i));
                count++;
            }
        }
        setValue(count == 0 ? "None" : sb.toString());
    }

    public void cycle() {
        if (multi) return;
        index++;
        if (index >= modes.size()) index = 0;
        setValue(modes.get(index));
    }

    public List<String> getModes() {
        return modes;
    }
}