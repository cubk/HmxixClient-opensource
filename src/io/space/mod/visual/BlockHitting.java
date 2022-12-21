package io.space.mod.visual;

import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;

public final class BlockHitting extends Mod {
    public static final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{
            "Vanilla",
            "None",
            "Exhibition",
            "HaoGe",
            "Slowly"
    });

    public static BlockHitting Instance;

    public static final NumberValue translatedX = new NumberValue("TranslatedX",0,-1,1,0.1);
    public static final NumberValue translatedY = new NumberValue("TranslatedY",0,-1,1,0.1);
    public static final NumberValue translatedZ = new NumberValue("TranslatedZ",0,-1,1,0.1);
    public static final NumberValue swingX = new NumberValue("SwingX",0,-1,1,0.1);
    public static final NumberValue swingY = new NumberValue("SwingY",0,-1,1,0.1);
    public static final NumberValue swingZ = new NumberValue("SwingZ",0,-1,1,0.1);
    public static final NumberValue swingSpeed = new NumberValue("SwingSpeed",1,0.1,1.5,0.1);
    public static final NumberValue blockSwingSpeed = new NumberValue("BlockSwingSpeed",1,0.1,1.5,0.1);
    public static final BooleanValue betterModel = new BooleanValue("BetterModel",true);
    public static final BooleanValue smoothHit = new BooleanValue("SmoothHit",true);

    public BlockHitting() {
        super("BlockHitting",Category.VISUAL);
        registerValues(mode,translatedX,translatedY,translatedZ,swingX,swingY,swingZ,swingSpeed,blockSwingSpeed,betterModel,smoothHit);
        Instance = this;
    }
}
