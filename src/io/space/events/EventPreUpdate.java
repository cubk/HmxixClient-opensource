package io.space.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public final class EventPreUpdate extends EventCancellable {
    private double x,y,z;
    private float yaw,pitch;
    private boolean onGround,sprinting,sneaking;

    public EventPreUpdate(double x, double y, double z, float yaw, float pitch, boolean onGround,boolean sprinting,boolean sneaking) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.sprinting = sprinting;
        this.sneaking = sneaking;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public void setVisualYaw(float yaw) {
        this.yaw = yaw;

        final EntityPlayerSP player = Minecraft.getMinecraft().player;

        player.rotationYawHead = yaw;
        player.renderYawOffset = yaw;
    }

    public void setVisualPitch(float pitch) {
        this.pitch = pitch;
        Minecraft.getMinecraft().player.rotationPitchHead = pitch;
    }
}
