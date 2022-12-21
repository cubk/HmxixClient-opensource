package io.space.mod.fight;

import io.space.mod.Mod;
import io.space.value.values.NumberValue;

public final class HitBox extends Mod {
    private static final NumberValue size = new NumberValue("Size", 0.1, 0.1, 1.0, 0.01);

    public static HitBox Instance;

    public HitBox() {
        super("HitBox",Category.FIGHT);
        registerValues(size);
        Instance = this;
    }

    @Override
    protected String getModTag() {
        return size.getValue().toString();
    }

    public static float getSize() {
        if (Instance.isEnable()) {
            return size.getValue().floatValue();
        }

        return 0.1F;
    }
}
