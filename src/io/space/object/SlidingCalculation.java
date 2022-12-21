package io.space.object;

import org.lwjgl.input.Mouse;

public final class SlidingCalculation {
    private double current;
    private double added;
    private double minus;

    public SlidingCalculation() {

    }

    public SlidingCalculation(double added, double minus) {
        this.added = added;
        this.minus = minus;
    }

    public SlidingCalculation(double current, double added, double minus) {
        this.current = current;
        this.added = added;
        this.minus = minus;
    }

    public void calculation() {
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();

            if (wheel != 0) {
                if (wheel < 0) {
                    current += added;
                } else {
                    current -= minus;
                }
            }
        }
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getAdded() {
        return added;
    }

    public void setAdded(double added) {
        this.added = added;
    }

    public double getMinus() {
        return minus;
    }

    public void setMinus(double minus) {
        this.minus = minus;
    }
}
