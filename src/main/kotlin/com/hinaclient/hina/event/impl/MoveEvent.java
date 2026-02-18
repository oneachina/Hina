/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package com.hinaclient.hina.event.impl;

import com.hinaclient.hina.event.Event;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public class MoveEvent extends Event {
    private final MoverType type;
    private Vec3 vec;

    public MoveEvent(MoverType type, Vec3 vec) {
        this.type = type;
        this.vec = vec;
    }

    public MoverType getType() { return type; }
    public Vec3 getVec() { return vec; }
    public void setVec(Vec3 vec) { this.vec = vec; }

    public void setX(double x) { this.vec = new Vec3(x, vec.y, vec.z); }
    public void setY(double y) { this.vec = new Vec3(vec.x, y, vec.z); }
    public void setZ(double z) { this.vec = new Vec3(vec.x, vec.y, z); }
}
