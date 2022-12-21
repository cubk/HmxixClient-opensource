package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import utils.hodgepodge.object.time.TimerUtils;

public final class Critical extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","Jump","LowMotion"});
    private final NumberValue delayValue = new NumberValue("Delay",100,0,1000,1);
    private final NumberValue hurtTime = new NumberValue("HurtTime",20,0,20,1);
    private final BooleanValue onGroundCheck = new BooleanValue("OnGroundCheck",true);
    private final BooleanValue speedCheck = new BooleanValue("SpeedCheck",true);
    private final BooleanValue tpAuraCheck = new BooleanValue("TPAuraCheck",true);

    private final TimerUtils delayTimerUtils = new TimerUtils();

    private final double[] vanillaOffset = new double[]{0.11,0.1100013579,0.0000013579};

    public Critical() {
        super("Critical",Category.FIGHT);
        registerValues(mode,delayValue,hurtTime,onGroundCheck,speedCheck,tpAuraCheck);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            if (cantCritical()) {
                return;
            }

            if (delayTimerUtils.hasReached(delayValue.getValue().longValue()) && ((C02PacketUseEntity) e.getPacket()).getEntityFromWorld(mc.world).hurtResistantTime <= hurtTime.getValue()) {
                switch (mode.getValue()) {
                    case "Vanilla":
                        critical(vanillaOffset);
                        break;
                    case "Jump":
                        if (mc.player.onGround) {
                            mc.player.jumpNoEvent();
                        }
                        break;
                    case "LowMotion":
                        if (mc.player.onGround) {
                            mc.player.motionY = 0.08;
                        }
                        break;
                }
            }
        }
    }

    private void critical(double[] value) {
        for(double offset : value) {
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean cantCritical() {
        if (onGroundCheck.getValue()) {
            if (!mc.player.onGround) return true;
        }

        if (speedCheck.getValue()) {
            if (ModManager.Instance.getModEnable("Speed")) {
                return true;
            }
        }

        if (tpAuraCheck.getValue()) {
            if (ModManager.Instance.getModEnable("TPAura")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
