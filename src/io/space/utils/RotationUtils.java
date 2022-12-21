package io.space.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public final class RotationUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getPredictedRotations(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + (double)(ent.getEyeHeight() / 2.0f);
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getBlockPosRotation(BlockPos pos) {
        return getRotationFromPosition(pos.getX(),pos.getZ(),pos.getY());
    }

    @SuppressWarnings("DuplicatedCode")
    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.player.posX;
        double zDiff = z - mc.player.posZ;
        double yDiff = y - mc.player.posY - 1.2;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
}
