package io.space.mod.move;

import io.space.mod.Mod;
import io.space.object.teleport.teleports.PacketTeleport;
import io.space.object.teleport.teleports.VanillaTeleport;
import io.space.value.values.ModeValue;

public final class Teleport extends Mod {
    private static final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","Packet"});

    public Teleport() {
        super("Teleport",Category.MOVE);
        registerValues(mode);
    }

    @Override
    protected void onEnable() {
        setEnable(false);
        super.onEnable();
    }

    public static void doTeleport(double x, double y, double z) {
        switch (mode.getValue()) {
            case "Vanilla":
                new VanillaTeleport(mc,x,y,z).teleportToXYZ();
                break;
            case "Packet":
                new PacketTeleport(mc,x,y,z).teleportToXYZ();
                break;
        }
        mc.renderGlobal.loadRenderers();
    }
}
