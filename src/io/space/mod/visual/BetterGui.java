package io.space.mod.visual;

import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;

public final class BetterGui extends Mod {
    public static final NumberValue scoreboardY = new NumberValue("ScoreboardY",0,-100,100,1);
    public static final BooleanValue scoreboardPoint = new BooleanValue("ScoreboardPoint",true);
    public static final BooleanValue scoreboardFont = new BooleanValue("ScoreboardFont",true);
    public static final BooleanValue chatFont = new BooleanValue("ChatFont",true);
    public static final BooleanValue chatAnimation = new BooleanValue("ChatAnimation",false);

    public static BetterGui Instance;

    public BetterGui() {
        super("BetterGui",Category.VISUAL);
        registerValues(scoreboardY,scoreboardPoint,scoreboardFont,chatFont,chatAnimation);
        Instance = this;
    }
}
