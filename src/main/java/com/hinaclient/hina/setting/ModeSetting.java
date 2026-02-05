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

package com.hinaclient.hina.setting;

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