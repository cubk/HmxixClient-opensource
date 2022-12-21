package io.space.mod.visual;

import io.space.mod.Mod;
import io.space.value.values.ColorValue;
import io.space.value.values.NumberValue;

public final class EntityHurtColor extends Mod {
    public static final ColorValue color = new ColorValue("Color",1,1,1);
    public static final NumberValue alpha = new NumberValue("Alpha",76.5,0,255,0.1);

    public static EntityHurtColor Instance;

    public EntityHurtColor() {
        super("EntityHurtColor",Category.VISUAL);
        registerValues(color,alpha);
        Instance = this;
    }
}
