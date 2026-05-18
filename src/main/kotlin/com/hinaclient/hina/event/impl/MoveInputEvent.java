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
package com.hinaclient.hina.event.impl;

import com.hinaclient.hina.event.Event;

public class MoveInputEvent extends Event {
    private float forward;
    private float strafe;
    private boolean jumping;
    private boolean sneaking;

    public MoveInputEvent(float forward, float strafe, boolean jumping, boolean sneaking) {
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    public float getForward() { return forward; }
    public void setForward(float forward) { this.forward = forward; }

    public float getStrafe() { return strafe; }
    public void setStrafe(float strafe) { this.strafe = strafe; }

    public boolean isJumping() { return jumping; }
    public void setJumping(boolean jumping) { this.jumping = jumping; }

    public boolean isSneaking() { return sneaking; }
    public void setSneaking(boolean sneaking) { this.sneaking = sneaking; }
}
