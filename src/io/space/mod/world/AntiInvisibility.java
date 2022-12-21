package io.space.mod.world;

import io.space.mod.Mod;

public final class AntiInvisibility extends Mod {
    public static AntiInvisibility Instance;

    public AntiInvisibility() {
        super("AntiInvisibility",Category.WORLD);
        Instance = this;
    }
}
