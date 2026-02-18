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

package com.hinaclient.hina.utils;

import org.lwjgl.glfw.GLFW;

public class KeyUtil {
    public static String getKeyName(int key) {
        if (key == -1) return "NONE";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name == null) {
            return switch (key) {
                case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
                case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
                case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
                case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
                case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
                case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
                case GLFW.GLFW_KEY_SPACE -> "SPACE";
                case GLFW.GLFW_KEY_ENTER -> "ENTER";
                case GLFW.GLFW_KEY_TAB -> "TAB";
                default -> "KEY_" + key;
            };
        }
        return name.toUpperCase();
    }
}
