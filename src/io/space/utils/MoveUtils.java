package io.space.utils;

import io.space.events.EventMove;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public final class MoveUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void setSpeed(double speed) {
        mc.player.motionX = -Math.sin(getDirection()) * speed;
        mc.player.motionZ = Math.cos(getDirection()) * speed;
    }

    public static float getDirection() {
        float yaw = mc.player.rotationYaw;
        if (mc.player.movementInput.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.player.movementInput.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.player.movementInput.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.player.movementInput.moveStrafe > 0.0f) {
            yaw -= 90.0f * forward;
        }
        if (mc.player.movementInput.moveStrafe < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw * ((float) Math.PI / 180);
    }

    public static double getMotionX(double speed) {
        return -Math.sin(getDirection()) * speed;
    }

    public static double getMotionZ(double speed) {
        return Math.cos(getDirection()) * speed;
    }

    public static double getBaseSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getNormalSpeedEffect() {
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            return mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }

        return 0;
    }

    public static void pause(EventMove e) {
        setSpeedEvent(e,0);
        setSpeed(0);
    }

    public static boolean isOnIce() {
        for (double i = 0; i < 1.5; i += 0.1) {
            final Block block = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - i, mc.player.posZ)).getBlock();
            if (block instanceof BlockIce || block instanceof BlockPackedIce) {
                return true;
            }
        }

        return false;
    }

    public static void setSpeedEvent(EventMove event, double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            event.setX(forward * speed * cos + strafe * speed * sin);
            event.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    public static int getJumpEffect() {
        if (mc.player.isPotionActive(Potion.jump)) {
            return mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        }
        return 0;
    }
}
