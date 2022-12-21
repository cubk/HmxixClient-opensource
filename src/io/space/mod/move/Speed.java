package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventJump;
import io.space.events.EventMove;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.utils.PlayerUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

import java.util.concurrent.ThreadLocalRandom;

public final class Speed extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Watchdog",new String[]{"Watchdog",
            "MineMora",
            "MineMoraLowHop",
            "SlowHop",
            "MineLand",
            "MineLandLowHop",
            "MinePlex",
            "Custom",
            "NCPHop",
            "DoubleJump",
            "AutoJump",
    });
    private final NumberValue watchdogBHopTimer = new NumberValue("WatchdogBHopTimer",1.0,1.0,10.0,0.01);
    private final NumberValue customMotionY = new NumberValue("CustomMotionY",0.42,0.0,2.0,0.01);
    private final NumberValue customSpeed = new NumberValue("CustomSpeed",0.26,0.0,2.0,0.01);
    private final NumberValue customTimer = new NumberValue("CustomTimer",1.0,0.01,10.0,0.01);
    private final BooleanValue customFakeGround = new BooleanValue("CustomFakeGround",false);

    public static double movementSpeed;

    private boolean watchdogBHopJumped = false;

    private int ncpTicks = 0;

    public Speed() {
        super("Speed",Category.MOVE);
        registerValues(mode,watchdogBHopTimer,customMotionY,customSpeed,customTimer,customFakeGround);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("Watchdog")) {
            if(mc.player.onGround) {
                double distX = mc.player.posX - mc.player.lastTickPosX;
                double distZ = mc.player.posZ - mc.player.lastTickPosZ;

                for(int i = 0; i < (int) 1; i++) {
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), true));

                    if(mc.world.getBlockState(new BlockPos(mc.player.posX + distX * 3, mc.player.posY, mc.player.posZ + distZ * 3)).getBlock() instanceof BlockAir) {
                        mc.player.setPosition(mc.player.posX + distX, mc.player.posY, mc.player.posZ + distZ);
                        e.setX(mc.player.posX);
                        e.setZ(mc.player.posZ);
                    }
                }
            }
        } else if (mode.isCurrentMode("MineMora")) {
            if (mc.player.isMoving()) {
                if (mc.player.isPotionActive(Potion.moveSpeed)) {
                    movementSpeed = MoveUtils.getBaseSpeed() * 0.87;
                } else movementSpeed = MoveUtils.getBaseSpeed() * 0.97;

                if (mc.player.onGround) {
                    mc.player.jumpNoEvent();
                }

                MoveUtils.setSpeed(movementSpeed);
            } else {
                MoveUtils.setSpeed(0);
                mc.timer.timerSpeed = 1;
            }
        } else if (mode.isCurrentMode("SlowHop")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jumpNoEvent();
                }
                MoveUtils.setSpeed(0.26 + (MoveUtils.getNormalSpeedEffect() * 0.05));
            } else {
                MoveUtils.setSpeed(0);
            }

            movementSpeed = 0.26;
        } else if (mode.isCurrentMode("MineLand")) {
            if (mc.player.isMoving()) {
                movementSpeed = 0.27 + MoveUtils.getNormalSpeedEffect() * 0.05;

                if (MoveUtils.isOnIce()) {
                    movementSpeed *= 1.5;
                }

                if (mc.player.onGround) {
                    mc.player.jumpNoEvent();
                }

                MoveUtils.setSpeed(movementSpeed);
            } else {
                MoveUtils.setSpeed(0);
            }
        } else if (mode.isCurrentMode("Custom")) {
            if (customFakeGround.getValue()) {
                e.setOnGround(true);
            }

            movementSpeed = customSpeed.getValue();

            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.motionY = customMotionY.getValue();
                }

                MoveUtils.setSpeed(movementSpeed);
            } else MoveUtils.setSpeed(0);

            if (!customTimer.getValue().equals(1.0)) {
                mc.timer.timerSpeed = customTimer.getValue().floatValue();
            }
        } else if (mode.isCurrentMode("WatchdogGround")) {
            if (mc.player.fallDistance < 2) {
                e.setOnGround(true);
            }
        } else if (mode.isCurrentMode("WatchdogBHop")) {
            if (watchdogBHopJumped) {
                mc.timer.timerSpeed = watchdogBHopTimer.getValue().floatValue();
            }
            if (mc.player.isMoving() && mc.player.onGround) {
                if (watchdogBHopJumped) {
                    mc.player.jumpNoEvent();
                    MoveUtils.setSpeed(movementSpeed = 0.36114514);
                } else {
                    mc.player.jumpNoEvent();
                    watchdogBHopJumped = true;
                }
            } else {
                movementSpeed = 0.18114514;
            }
        } else if (mode.isCurrentMode("MinePlex")) {
            if(mc.player.isCollidedHorizontally) {
                movementSpeed = 0.15;
            } else {
                movementSpeed = 0.38 + MoveUtils.getNormalSpeedEffect() * 0.02;
            }

            if(!mc.player.isInLiquid() && mc.player.onGround && mc.player.isMoving()) {
                mc.player.motionY = 0.42 + MoveUtils.getJumpEffect() * 0.05;
                MoveUtils.setSpeed(movementSpeed);
            }

            if(mc.player.isInLiquid()) {
                movementSpeed = 0.22;
            }
        } else if (mode.isCurrentMode("DoubleJump")) {
            if (mc.player.isMoving() && mc.player.onGround) {
                mc.player.jumpNoEvent();
                mc.player.jumpNoEvent();
            }
        } else if (mode.isCurrentMode("AutoJump")) {
            if (mc.player.isMoving() && mc.player.onGround) {
                mc.player.jumpNoEvent();
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if (mode.isCurrentMode("Watchdog")) {
            if (mc.player.isMoving()) {
                movementSpeed = 0.31;

                if (mc.player.onGround) {
                    e.setY(mc.player.motionY = 0.2);
                }

                if (!mc.player.isSneaking()) {
                    MoveUtils.setSpeedEvent(e, movementSpeed);
                }

                mc.player.motionY -= ThreadLocalRandom.current().nextDouble(0.07, 0.1);
            } else {
                MoveUtils.pause(e);
            }
        } else if (mode.isCurrentMode("WatchdogGround")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    movementSpeed = 0.31 + MoveUtils.getNormalSpeedEffect() * 0.03;

                    if (!mc.player.isSneaking()) {
                        MoveUtils.setSpeedEvent(e, movementSpeed);
                    }
                }
            } else {
                MoveUtils.pause(e);
            }
        } else if (mode.isCurrentMode("MineMoraLowHop")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jumpNoEvent();
                    mc.player.motionY = 0;
                    e.setY(0.41999998688698);
                }

                movementSpeed = 0.27 + (MoveUtils.getNormalSpeedEffect() * 0.05);
                MoveUtils.setSpeed(movementSpeed);
            }
        } else if (mode.isCurrentMode("MineLandLowHop")) {
            if (mc.player.isMoving()) {
                if (!mc.player.isSneaking()) {
                    if (mc.player.onGround) {
                        mc.player.jumpNoEvent();
                        e.setY(mc.player.motionY = 0.4);

                        MoveUtils.setSpeed(1);
                    }

                    movementSpeed = 0.33 + MoveUtils.getNormalSpeedEffect() * 0.05;

                    if (MoveUtils.isOnIce()) {
                        movementSpeed *= 1.5;
                    }

                    MoveUtils.setSpeed(movementSpeed);
                    final Block block = mc.world.getBlock(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
                    if (!mc.player.isCollidedHorizontally && !(block instanceof BlockAir) && !(block instanceof BlockStairs) && !(block instanceof BlockSlab) && mc.player.fallDistance < 1) {
                        mc.player.motionY = mc.player.ticksExisted % 2 == 1 ? 0 : -0.1;
                    }
                }
            } else MoveUtils.pause(e);
        } else if (mode.isCurrentMode("NCPHop")) {
            movementSpeed = 0.2695;

            if (mc.player.isPotionActive(Potion.moveSpeed)) {
                movementSpeed *= 1.0 + 0.09 * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
            }

            if (mc.player.isMoving() && !mc.player.isInLiquid()) {
                if (mc.player.onGround) {
                    e.setY(mc.player.motionY = 0.42);
                    MoveUtils.setSpeedEvent(e,movementSpeed * 1.5);
                    ncpTicks = 0;
                } else {
                    MoveUtils.setSpeedEvent(e,movementSpeed * 1.18 - (ncpTicks / 1520.0));
                }

                if (ncpTicks < 15) ncpTicks++;
            } else {
                ncpTicks = 0;
                movementSpeed = 0.26;
            }
        }
    }

    @EventTarget
    public void onJump(EventJump e) {
        e.cancelEvent();
    }

    @Override
    protected void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        watchdogBHopJumped = false;
        ncpTicks = 0;

        super.onDisable();
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
