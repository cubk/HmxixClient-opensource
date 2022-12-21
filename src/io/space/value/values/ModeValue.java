package io.space.value.values;

import io.space.value.Value;
import io.space.value.ValueType;

public class ModeValue extends Value<String> {
    private final String[] modes;

    public boolean shouldShow;

    public ModeValue(String valueName,String defaultValue,String[] modes) {
        super(valueName, ValueType.MODE_VALUE);
        this.modes = modes;
        setValueDirect(defaultValue);
    }

    public boolean isCurrentMode(String mode) {
        return getValue().equals(mode);
    }

    public String[] getModes() {
        return modes;
    }
}
