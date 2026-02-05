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

import java.util.function.Supplier;

/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
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