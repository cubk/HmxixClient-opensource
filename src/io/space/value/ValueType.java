package io.space.value;

public enum ValueType {
    BOOLEAN_VALUE("BOOLEAN_VALUE"),
    NUMBER_VALUE("NUMBER_VALUE"),
    MODE_VALUE("MODE_VALUE"),
    TEXT_VALUE("TEXT_VALUE"),
    COLOR_VALUE("COLOR_VALUE"),
    NULL("NULL");

    private final String name;

    ValueType(String name) {
        this.name = name;
    }

    public static ValueType get(String str) {
        for (ValueType type : values()) {
            if (type.toString().toLowerCase().equalsIgnoreCase(str)) {
                return type;
            }
        }

        return NULL;
    }

    @Override
    public String toString() {
        return name;
    }
}
