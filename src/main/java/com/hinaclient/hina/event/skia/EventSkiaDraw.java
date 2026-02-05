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

package com.hinaclient.hina.event.skia;

import com.hinaclient.hina.event.Event;

/**
 * @Author: oneachina
 * @Date: 2026/2/3 11:33
 */
public class EventSkiaDraw extends Event {
    public static final EventSkiaDraw INSTANCE = new EventSkiaDraw();
    private EventSkiaDraw() {}
}