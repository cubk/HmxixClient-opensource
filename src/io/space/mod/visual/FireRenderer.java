package io.space.mod.visual;

import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;

public final class FireRenderer extends Mod {
    public static final ModeValue mode = new ModeValue("Mode","Low",new String[]{"Low","None"});
    public static final NumberValue lowY = new NumberValue("LowY",0.0,0.0,1.0,0.01);

    public static FireRenderer Instance;

    public FireRenderer() {
        super("FireRenderer",Category.VISUAL);
        registerValues(mode,lowY);
        Instance = this;
    }
}
