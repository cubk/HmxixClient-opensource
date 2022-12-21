package io.space.mod.visual;

import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.ColorValue;

public final class EnchantColor extends Mod {
    public static final ColorValue colorValue = new ColorValue("Color",255,0,0);
    public static final BooleanValue rainbow = new BooleanValue("Rainbow",false);

    public static EnchantColor Instance;

    public EnchantColor() {
        super("EnchantColor",Category.VISUAL);
        registerValues(colorValue,rainbow);

        Instance = this;
    }
}
