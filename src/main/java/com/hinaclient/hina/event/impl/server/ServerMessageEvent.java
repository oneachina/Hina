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

package com.hinaclient.hina.event.impl.server;

import com.hinaclient.hina.event.Event;
import net.minecraft.network.chat.Component;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 11:48
 */
public class ServerMessageEvent extends Event {
    private Component message;
    private boolean overlay;

    public ServerMessageEvent(Component message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public void setOverlay(boolean overlay) {
        this.overlay = overlay;
    }
}
