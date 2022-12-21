package io.space.mod.move;

import io.space.mod.Mod;

public final class SafeWalk extends Mod {
    public static SafeWalk Instance;

    public SafeWalk() {
        super("SafeWalk",Category.MOVE);
        Instance = this;
    }
}
