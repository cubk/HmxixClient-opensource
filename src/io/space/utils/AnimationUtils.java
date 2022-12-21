package io.space.utils;

public final class AnimationUtils {
    public static double easing(double now,double target,double speed) {
        return Math.abs(target - now) * speed;
    }
}
