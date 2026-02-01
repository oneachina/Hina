/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.setting;

import java.util.function.Supplier;

public abstract class Setting<T> {
    private final String name;
    private T value;
    private Supplier<Boolean> visibility;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
        this.visibility = () -> true;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visibility.get();
    }

    public Setting<T> setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }
}