package io.space.value.values;

import io.space.value.Value;
import io.space.value.ValueType;

public class BooleanValue extends Value<Boolean> {
    public double animationX;

    public BooleanValue(String valueName,boolean defaultValue) {
        super(valueName, ValueType.BOOLEAN_VALUE);
        setValueDirect(defaultValue);
    }
}
