package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.PlayerUtils;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class AntiVoid extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Packet",new String[]{"Packet","Motion","Pause","SpoofY"});
    private final NumberValue motionY = new NumberValue("MotionY",1.114514,0.0,10.0,0.01);

    private boolean already;

    public AntiVoid() {
        super("AntiVoid",Category.MOVE);
        registerValues(mode,motionY);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("Packet")) {
            if (PlayerUtils.isOverVoid() && mc.player.fallDistance > 3) {
                mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX,mc.player.posY + 11.4514,mc.player.posZ,false));
            }
        } else if (mode.isCurrentMode("Motion")) {
            if (PlayerUtils.isOverVoid() && mc.player.fallDistance > 3) {
                if (!already) {
                    mc.player.motionY = motionY.getValue();
                    already = true;
                }
            } else {
                already = false;
            }
        } else if (mode.isCurrentMode("Pause")) {
            if (PlayerUtils.isOverVoid() && mc.player.fallDistance > 3) {
                mc.player.motionY = 0;
            }
        } else if (mode.isCurrentMode("SpoofY")) {
            if (PlayerUtils.isOverVoid() && mc.player.fallDistance > 3) {
                e.setY(e.getY() + 11.4514);
            }
        }
    }
}
