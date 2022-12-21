package io.space.mod.move;

import io.space.mod.Mod;
import io.space.value.values.BooleanValue;

public final class BlockSpeed extends Mod {
    public static final BooleanValue soulSand = new BooleanValue("SoldSand",true);

    public static BlockSpeed Instance;

    public BlockSpeed() {
        super("BlockSpeed",Category.MOVE);
        registerValues(soulSand);
        Instance = this;
    }
}
