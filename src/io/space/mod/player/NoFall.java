package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.PlayerUtils;
import io.space.value.values.ModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFall extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Vanilla",new String[]{"Vanilla","Packet","Pause","Matrix"});

    public NoFall() {
        super("NoFall",Category.PLAYER);
        registerValues(mode);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("Vanilla")) {
            if (mc.player.fallDistance > 3.0f && !PlayerUtils.isOverVoid()) {
                e.setOnGround(true);
            }
        } else if (mode.isCurrentMode("Packet")) {
            if (mc.player.fallDistance > 3.0f && !PlayerUtils.isOverVoid()) {
                mc.getNetHandler().sendPacket(new C03PacketPlayer(true));
            }
        } else if (mode.isCurrentMode("Pause")) {
            if (mc.player.fallDistance > 3.0f && !PlayerUtils.isOverVoid()) {
                e.setOnGround(true);
                mc.player.fallDistance = 0.0f;
                mc.player.motionY = 0.0;
            }
        } else if (mode.isCurrentMode("Matrix")) {
            if (mc.player.fallDistance > 3.0f && !PlayerUtils.isOverVoid()) {
                mc.getNetHandler().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.player.fallDistance = 0.0f;
            }
        }
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
