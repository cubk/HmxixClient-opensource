package io.space.utils.pathfinder;

import net.minecraft.util.Vec3;

public final class SigmaVec3 {
    private final double x;
    private final double y;
    private final double z;

    public SigmaVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public SigmaVec3 addVector(double x, double y, double z) {
        return new SigmaVec3(this.x + x, this.y + y, this.z + z);
    }

    public SigmaVec3 floor() {
        return new SigmaVec3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public double squareDistanceTo(SigmaVec3 v) {
        return Math.pow(v.x - this.x, 2.0) + Math.pow(v.y - this.y, 2.0) + Math.pow(v.z - this.z, 2.0);
    }

    public SigmaVec3 add(SigmaVec3 v) {
        return this.addVector(v.getX(), v.getY(), v.getZ());
    }

    public Vec3 mc() {
        return new Vec3(this.x, this.y, this.z);
    }

    public String toString() {
        return "[" + this.x + ";" + this.y + ";" + this.z + "]";
    }
}
