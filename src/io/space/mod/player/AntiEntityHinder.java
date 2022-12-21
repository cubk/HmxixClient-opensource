package io.space.mod.player;

import io.space.mod.Mod;

public final class AntiEntityHinder extends Mod {
    public static AntiEntityHinder Instance;

    public AntiEntityHinder() {
        super("AntiEntityHinder",Category.PLAYER);
        Instance = this;
    }
}
