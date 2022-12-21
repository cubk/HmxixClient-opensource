package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.MoveUtils;
import io.space.utils.PlayerUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;

public final class LongJump extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","MineMora","Watchdog"});
    private final NumberValue vanillaSpeed = new NumberValue("VanillaSpeed",1.0,0.0,10.0,0.1);
    private final BooleanValue toggle = new BooleanValue("Toggle",true);

    private int airTicks = 0;

    public LongJump() {
        super("LongJump",Category.MOVE);
        registerValues(mode,vanillaSpeed,toggle);
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        if (airTicks > 0 && mc.player.onGround) {
            airTicks = 0;

            if (mode.isCurrentMode("Vanilla") || mode.isCurrentMode("MineMora")) {
                MoveUtils.setSpeed(0);
            }

            if (toggle.getValue()) {
                NotificationManager.Instance.addNotification("LongJump","LongJump disable (Auto)", Notification.NotificationType.WARNING,1000);
                toggle();
                return;
            }
        }

        if (mode.isCurrentMode("Vanilla")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.motionY = 0.41;
                } else {
                    MoveUtils.setSpeed(Math.max(vanillaSpeed.getValue(), MoveUtils.getBaseSpeed()));
                    airTicks++;
                }
            }
        } else if (mode.isCurrentMode("MineMora")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.timer.timerSpeed = 0.1f;
                    mc.player.motionY = 0.41;
                } else {
                    mc.timer.timerSpeed = 1f;
                    MoveUtils.setSpeed(Math.max(0.55, MoveUtils.getBaseSpeed()));
                    airTicks++;
                }
            }
        } else if (mode.isCurrentMode("Watchdog")) {
            if (mc.player.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jumpNoEvent();
                } else {
                    if (airTicks < 2) {
                        mc.player.setPositionAndUpdate(mc.player.posX + MoveUtils.getMotionX(1), mc.player.posY, mc.player.posZ + MoveUtils.getMotionZ(1));
                    } else {
                        MoveUtils.setSpeed(0);
                    }

                    airTicks++;
                }
            }
        }
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
