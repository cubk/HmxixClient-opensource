package io.space.mod.visual;

import io.space.mod.Mod;

public final class NoHurtCam extends Mod {
    public static NoHurtCam Instance;

    public NoHurtCam() {
        super("NoHurtCam",Category.VISUAL);
        Instance = this;
    }
}
