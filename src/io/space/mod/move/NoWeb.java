package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventCollideWithBlock;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.value.values.ModeValue;
import net.minecraft.block.BlockWeb;
import net.minecraft.util.AxisAlignedBB;

public final class NoWeb extends Mod {
    public static final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","AntiCheat","Collide"});

    public static NoWeb Instance;

    public NoWeb() {
        super("NoWeb",Category.MOVE);
        registerValues(mode);
        Instance = this;
    }

    @EventTarget
    public void onCollideWithBlock(EventCollideWithBlock e) {
        if (mode.isCurrentMode("Collide")) {
            if (e.getBlock() instanceof BlockWeb) {
                final int x = e.getBlockPos().getX();
                final int y = e.getBlockPos().getY();
                final int z = e.getBlockPos().getZ();

                e.setBoundingBox(new AxisAlignedBB(x,y,z,x + 1,y + 1,z + 1));
            }
        } else if (mode.isCurrentMode("AntiCheat")) {
            if (e.getBlock() instanceof BlockWeb && mc.player.isMoving()) {
                MoveUtils.setSpeed(0.23);
            }
        }
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
