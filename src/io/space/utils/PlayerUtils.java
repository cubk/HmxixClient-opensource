package io.space.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public final class PlayerUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float getDirection(float yaw) {
        return yaw * ((float) Math.PI / 180.0f);
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollidingBoundingBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public static boolean isOnLiquid() {
        AxisAlignedBB boundingBox = mc.player.getEntityBoundingBox();

        if (boundingBox == null) {
            return false;
        }

        boundingBox = boundingBox.contract(0.01, 0.0, 0.01).offset(0.0, -0.01, 0.0);
        boolean onLiquid = false;
        int y = (int) boundingBox.minY;
        for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper.floor_double(boundingBox.maxX + 1.0); ++x) {
            for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper.floor_double(boundingBox.maxZ + 1.0); ++z) {
                Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block == Blocks.air) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                onLiquid = true;
            }
        }

        return onLiquid;
    }

    public static boolean isOverVoid() {
        boolean isOverVoid = true;
        BlockPos block = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
        for (double i = mc.player.posY + 1; i > 0; i -= 0.5) {
            if (mc.world.getBlockState(block).getBlock() != Blocks.air) {
                isOverVoid = false;
                break;
            }
            block = block.add(0, -1, 0);
        }

        for (double i = 0; i < 10; i += 0.1) {
            if (isOnGround(i) && isOverVoid) {
                isOverVoid = false;
                break;
            }
        }

        return isOverVoid;
    }
}
