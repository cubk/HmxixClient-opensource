package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.*;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.utils.PlayerUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public final class Fly extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","Watchdog","AirWalk","PlayMC","BlocksMC","HyCraft","AirGround","CoralMC"});
    private final NumberValue vanillaSpeed = new NumberValue("VanillaSpeed",1.0,0.0,10.0,0.01);
    private final NumberValue vanillaMotionSpeed = new NumberValue("VanillaMotion",1.0,0.0,10.0,0.01);
    private final NumberValue airWalkSpeed = new NumberValue("AirWalkSpeed",0.26,0.0,10.0,0.01);
    private final BooleanValue bobbing = new BooleanValue("Bobbing",false);

    public static double moveSpeed;

    private double hyCraftStartY;
    private double coralMCStartY;

    public Fly() {
        super("Fly",Category.MOVE);
        registerValues(mode,vanillaSpeed,vanillaMotionSpeed,airWalkSpeed,bobbing);
    }

    @EventTarget
    public void on3D(Event3D e) {
        if (bobbing.getValue()) {
            mc.player.cameraYaw = 0.09090908616781235f;
        }
    }

    @EventTarget
    public void onCollideWithBlock(EventCollideWithBlock e) {
        if (mode.isCurrentMode("AirGround")) {
            if (e.getBlock() instanceof BlockAir) {
                final int y = e.getBlockPos().getY();

                if (y < mc.player.posY) {
                    final int x = e.getBlockPos().getX();
                    final int z = e.getBlockPos().getZ();
                    e.setBoundingBox(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
                }
            }
        }
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("Vanilla")) {
            moveSpeed = vanillaSpeed.getValue();
            mc.player.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? vanillaMotionSpeed.getValue() : mc.gameSettings.keyBindSneak.isKeyDown() ? -vanillaMotionSpeed.getValue() : 0;

            if (mc.player.isMoving()) {
                MoveUtils.setSpeed(vanillaSpeed.getValue());
            } else {
                MoveUtils.setSpeed(0);
            }
        } else if (mode.isCurrentMode("Watchdog")) {
            e.setOnGround(true);

            e.setY(e.getY() + 0.01);

            moveSpeed = 0.26;
            mc.player.motionY = 0;

            if (!mc.player.isMoving()) {
                MoveUtils.setSpeed(0);
            }
        } else if (mode.isCurrentMode("AirWalk")) {
            mc.player.motionY = 0;
            if (mc.player.isMoving()) {
                MoveUtils.setSpeed(airWalkSpeed.getValue());
            } else {
                MoveUtils.setSpeed(0);
            }
        } else if (mode.isCurrentMode("PlayMC")) {
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX + MoveUtils.getMotionX(3), mc.player.posY + 11.4514, mc.player.posZ + MoveUtils.getMotionZ(3), false));
        } else if (mode.isCurrentMode("BlocksMC")) {
            mc.player.motionY = mc.player.ticksExisted % 2 == 0 ? 0.1 : -0.1;

            if (mc.player.isMoving()) {
                MoveUtils.setSpeed(moveSpeed = 0.8);
            } else {
                MoveUtils.setSpeed(0);
            }
        } else if (mode.isCurrentMode("HyCraft")) {
            if (mc.player.posY <= hyCraftStartY) {
                e.setOnGround(true);
                mc.player.motionY = 0.1;
            }
        } else if (mode.isCurrentMode("CoralMC")) {
            if (mc.player.ticksExisted % 2 == 0) {
                e.setOnGround(true);
            }

            if (mc.player.posY < coralMCStartY && PlayerUtils.isOverVoid()) {
                MoveUtils.setSpeed(0.5);
                mc.player.motionY = 0.7;
                e.setOnGround(true);
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if (mode.isCurrentMode("PlayMC")) {
            MoveUtils.setSpeedEvent(e,0);
            e.setY(mc.player.motionY = 0);
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (mode.isCurrentMode("PlayMC")) {
            Wrapper.sendMessage("只能在虚空中飞!");
        } else if (mode.isCurrentMode("BlocksMC")) {
            if (mc.player.onGround) {
                mc.player.jumpNoEvent();
            }
        } else if (mode.isCurrentMode("HyCraft")) {
            if (mc.player.onGround) {
                mc.player.jumpNoEvent();
            }

            hyCraftStartY = mc.player.posY;
        } else if (mode.isCurrentMode("CoralMC")) {
            coralMCStartY = mc.player.posY - 5;
        }
    }

    @Override
    protected void onDisable() {
        if (mode.isCurrentMode("Vanilla") || mode.isCurrentMode("AirWalk")) {
            MoveUtils.setSpeed(0);
        }

        if (mode.isCurrentMode("BlocksMC")) {
            MoveUtils.setSpeed(0);
            mc.player.motionY = 0;
        }

        mc.timer.timerSpeed = 1f;

        super.onDisable();
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
