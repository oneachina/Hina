package com.eatgrapes.hina.utils;

import org.lwjgl.glfw.GLFW;

/**
 * @Author: oneachina
 * @Date: 2026/2/2 13:52
 */
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
