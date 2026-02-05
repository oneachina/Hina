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

package com.hinaclient.hina.module;

import com.hinaclient.hina.event.EventBus;
import com.hinaclient.hina.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private boolean enabled;
    private final Category category;
    private int key = -1;
    private final List<Setting<?>> settings = new ArrayList<>();
    public final Minecraft client = Minecraft.getInstance();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) onEnable();
        else onDisable();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.enabled) onEnable();
        else onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    protected void onEnable() {
        EventBus.INSTANCE.register(this);
    }
    protected void onDisable() {
        EventBus.INSTANCE.unregister(this);
    }
}