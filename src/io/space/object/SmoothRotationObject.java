package io.space.object;

public final class SmoothRotationObject extends RotationObject {
    private float willYaw,willPitch;

    public void handleRotation(double speed) {
        rotationWithSpeed(willYaw,willPitch,speed);
    }

    public RotationObject setWillYawPitch(float yaw, float pitch) {
        setWillYaw(yaw);
        setWillPitch(pitch);

        return this;
    }

    public float getWillYaw() {
        return willYaw;
    }

    public void setWillYaw(float willYaw) {
        this.willYaw = willYaw;
    }

    public float getWillPitch() {
        return willPitch;
    }

    public void setWillPitch(float willPitch) {
        this.willPitch = willPitch;
    }
}
