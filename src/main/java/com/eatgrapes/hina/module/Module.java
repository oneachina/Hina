/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module;

import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private String name;
    private boolean enabled;
    private Category category;
    private int key = -1;
    private final List<Setting<?>> settings = new ArrayList<>();
    public final MinecraftClient client = MinecraftClient.getInstance();

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