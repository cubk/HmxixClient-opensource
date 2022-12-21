package io.space.object;

import io.space.events.EventPreUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

@SuppressWarnings("DuplicatedCode")
public class RotationObject {
    protected final Minecraft mc = Minecraft.getMinecraft();

    private float yaw,pitch,prevYaw,prevPitch;

    public RotationObject() {
        yaw = 0;
        pitch = 0;
        prevPitch = 0;
        prevYaw = 0;
    }

    public RotationObject(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public RotationObject setYaw(float yaw) {
        update();
        this.yaw = yaw;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public float[] getYawPitch() {
        return new float[]{getYaw(),getPitch()};
    }

    public RotationObject setPitch(float pitch) {
        update();
        this.pitch = pitch;
        return this;
    }

    public RotationObject setYawPitch(float yaw,float pitch) {
        update();
        return setYaw(yaw).setPitch(pitch);
    }
    
    public RotationObject rotationWithSpeed(float targetYaw, float targetPitch,double speed) {
        update();
        float yawDifference = getAngleDifference(targetYaw, getYaw());
        float pitchDifference = getAngleDifference(targetPitch, getPitch());
        yaw += (float) (yawDifference > speed ? speed : Math.max(yawDifference, -speed));
        pitch += (float) (pitchDifference > speed ? speed : Math.max(pitchDifference, -speed));

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        float deltaYaw = yaw - prevYaw;
        deltaYaw -= deltaYaw % gcd;
        yaw = prevYaw + deltaYaw;
        float deltaPitch = pitch - prevPitch;
        deltaPitch -= deltaPitch % gcd;
        pitch = prevPitch + deltaPitch;
        return this;
    }

    public RotationObject rotationWithSpeedOnlyYaw(float targetYaw,double speed) {
        update();
        float yawDifference = getAngleDifference(targetYaw, getYaw());
        yaw += (float) (yawDifference > speed ? speed : Math.max(yawDifference, -speed));

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        float deltaYaw = yaw - prevYaw;
        deltaYaw -= deltaYaw % gcd;
        yaw = prevYaw + deltaYaw;
        return this;
    }

    public RotationObject rotationWithSpeedOnlyPitch(float targetPitch,double speed) {
        update();
        float pitchDifference = getAngleDifference(targetPitch, getPitch());
        pitch += (float) (pitchDifference > speed ? speed : Math.max(pitchDifference, -speed));

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        float deltaPitch = pitch - prevPitch;
        deltaPitch -= deltaPitch % gcd;
        pitch = prevPitch + deltaPitch;
        return this;
    }

    private void update() {
        prevYaw = yaw;
        prevPitch = pitch;
    }

    public void setEntityRotation(Entity entity) {
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
    }

    public void setPlayerRotation(EventPreUpdate e) {
        e.setVisualYaw(yaw);
        e.setVisualPitch(pitch);
    }

    public static float getAngleDifference(float a,float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    @Override
    public String toString() {
        return "RotationObject{" +
                "yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
