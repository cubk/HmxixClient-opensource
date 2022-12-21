package io.space.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public final class PositionUtils {
    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        x += face.getFrontOffsetX() / 2.0;
        z += face.getFrontOffsetZ() / 2.0;
        y += face.getFrontOffsetY() / 2.0;

        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += ThreadLocalRandom.current().nextDouble(-0.3, 0.3);
            z += ThreadLocalRandom.current().nextDouble(-0.3, 0.3);
        } else {
            y += ThreadLocalRandom.current().nextDouble(0.49, 0.5);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += ThreadLocalRandom.current().nextDouble(-0.3, 0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += ThreadLocalRandom.current().nextDouble(-0.3, 0.3);
        }
        return new Vec3(x, y, z);
    }
}
