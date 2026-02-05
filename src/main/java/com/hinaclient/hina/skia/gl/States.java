/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2025. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 */

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


package com.hinaclient.hina.skia.gl;

import static org.lwjgl.opengl.GL30.*;
import java.util.Stack;

/*
 *  Converted to Java by oneachina
 */
public class States {

    /**
     * The single instance of States.
     */
    public static final States INSTANCE = new States();

    /**
     * The current OpenGL version.
     */
    private final int glVersion;

    /**
     * The stack of OpenGL states.
     */
    private final Stack<State> states = new Stack<>();

    /**
     * Pushes the current OpenGL state onto the stack.
     */
    public static void push() {
        INSTANCE.states.push(new State(INSTANCE.glVersion).push());
    }

    /**
     * Pops the last OpenGL state from the stack and restores it.
     */
    public static void pop() {
        if (INSTANCE.states.isEmpty()) {
            throw new IllegalStateException("No state to restore.");
        }
        INSTANCE.states.pop().pop();
    }

    /**
     * Gets the current OpenGL version.
     * * This code was inspired by imgui-java and modified to fit the project's codebase.
     */
    private States() {
        int[] major = new int[1];
        int[] minor = new int[1];
        glGetIntegerv(GL_MAJOR_VERSION, major);
        glGetIntegerv(GL_MINOR_VERSION, minor);
        this.glVersion = major[0] * 100 + minor[0] * 10;
    }

    /**
     * Returns the glVersion if needed elsewhere.
     */
    public int getGlVersion() {
        return glVersion;
    }
}
