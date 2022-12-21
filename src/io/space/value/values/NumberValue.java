package io.space.value.values;

import io.space.value.Value;
import io.space.value.ValueType;

public class NumberValue extends Value<Double> {
    private final double increase;
    private final double min;
    private final double max;

    public double animationX;

    public NumberValue(String valueName,double defaultValue,double min,double max,double increase) {
        super(valueName, ValueType.NUMBER_VALUE);
        this.increase = increase;
        this.min = min;
        this.max = max;
        setValueDirect(defaultValue);
    }

    public NumberValue(String valueName,int defaultValue,int min,int max,int increase) {
        super(valueName, ValueType.NUMBER_VALUE);
        this.increase = increase;
        this.min = min;
        this.max = max;
        setValueDirect((double) defaultValue);
    }

    public double getIncrease() {
        return increase;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
