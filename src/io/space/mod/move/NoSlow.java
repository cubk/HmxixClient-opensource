package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventPacket;
import io.space.events.EventPostUpdate;
import io.space.events.EventPreUpdate;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class NoSlow extends Mod {
    public static final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","NCP","Watchdog","MineMora"});
    public static final NumberValue multiplier = new NumberValue("Multiplier",1.0,0.0,1.0,0.01);

    public static NoSlow Instance;

    public NoSlow() {
        super("NoSlow", Category.MOVE);
        registerValues(mode,multiplier);
        Instance = this;
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("NCP")) {
            if (mc.player.isRiding()) {
                return;
            }

            if (mc.player.isMoving() && mc.player.isBlocking()) {
                mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        } else if (mode.isCurrentMode("MineMora")) {
            if (mc.player.isMoving() && mc.player.isBlocking()) {
                mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        } else if (mode.isCurrentMode("Watchdog")) {
            if (mc.player.isUsingItem()) {
                if (mc.player.ticksExisted % 10 == 0) {
                    mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.player.getCurrentEquippedItem()));
                }
            }
        }
    }

    @EventTarget
    public void onPostUpdate(EventPostUpdate e) {
        if (mode.isCurrentMode("NCP")) {
            if (mc.player.isRiding()) {
                return;
            }

            if (mc.player.isMoving() && mc.player.isBlocking()) {
                mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.player.getItemInUse()));
            }
        } else if (mode.isCurrentMode("MineMora")) {
            if (mc.player.isMoving() && mc.player.isBlocking()) {
                mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.player.getItemInUse()));
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (mode.isCurrentMode("Watchdog")) {
            if (mc.player.isUsingItem() && e.getPacket() instanceof S30PacketWindowItems) {
                mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.player.inventory.getCurrentItem(), 0f, 0f, 0f));
                e.cancelEvent();
            }
        }
    }

    public static boolean onlyIncludesSword() {
        return mode.isCurrentMode("MineMora");
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
