package io.space.value;

public abstract class Value<Type> {
    private final String valueName;
    private final ValueType valueType;
    private Type value;

    public Value(String valueName,ValueType valueType) {
        this.valueName = valueName;
        this.valueType = valueType;
    }

    public Type getValue() {
        return value;
    }

    public void setValue(Type value) {
        if (onChangeValue(this.value,value)) {
            this.value = value;
        }
    }

    public void setValueDirect(Type value) {
        this.value = value;
    }

    public String getValueName() {
        return valueName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    protected boolean onChangeValue(Type preValue, Type postValue) {
        return true;
    }
}
