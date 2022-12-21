package io.space.mod.fight;

import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;

public final class Reach extends Mod {
    private static final NumberValue reachValue = new NumberValue("Reach",3.0,3.0,8.0,0.1);
    private static final BooleanValue onlyWhileSprinting = new BooleanValue("OnlyWhileSprinting",false);

    public static Reach Instance;

    public Reach() {
        super("Reach",Category.FIGHT);
        registerValues(reachValue,onlyWhileSprinting);
        Instance = this;
    }

    @Override
    protected String getModTag() {
        return reachValue.getValue().toString();
    }

    public static double getReachValue() {
        if (onlyWhileSprinting.getValue()) {
            if (mc.player.isSprinting())
                return reachValue.getValue();
            else
                return 3.0;
        }
        return reachValue.getValue();
    }
}
