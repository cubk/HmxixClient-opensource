package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import static io.space.utils.MoveUtils.getDirection;

public final class Velocity extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Normal",new String[]{"Normal","AAC","Hypixel"});
    private final NumberValue vertical = new NumberValue("Vertical",0.0, 0.0, 100.0, 1);
    private final NumberValue horizontal = new NumberValue("Horizontal",0.0, 0.0, 100.0, 1);
    private final BooleanValue explosionPacket = new BooleanValue("ExplosionPacket",true);

    public Velocity() {
        super("Velocity",Category.FIGHT);
        registerValues(mode,vertical,horizontal,explosionPacket);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();


            if (packet.getEntityID() == mc.player.getEntityId()) {
                if(mode.isCurrentMode("Hypixel")){
                    Wrapper.sendMessage("Velocity XMotion Modification: " + ((S12PacketEntityVelocity) e.getPacket()).motionX);
                    Wrapper.sendMessage("Velocity YMotion Modification: " + ((S12PacketEntityVelocity) e.getPacket()).motionY);
                    Wrapper.sendMessage("Velocity ZMotion Modification: " + ((S12PacketEntityVelocity) e.getPacket()).motionZ);

                    if (!mc.player.onGround) {
                        strafe(MoveUtils.getBaseSpeed() * 0.1);
                    }

                }
                if (mode.isCurrentMode("Normal")) {
                    if (vertical.getValue().equals(0.0) && horizontal.getValue().equals(0.0)) {
                        e.cancelEvent();
                    } else {
                        packet.motionX *= horizontal.getValue() / 100.0;
                        packet.motionY *= vertical.getValue() / 100.0;
                        packet.motionZ *= horizontal.getValue() / 100.0;
                    }
                } else if (mode.isCurrentMode("AAC")) {
                    if (mc.player.isMoving()) {
                        packet.motionX *= horizontal.getValue() / 100.0;
                        packet.motionZ *= horizontal.getValue() / 100.0;
                        mc.player.motionX *= horizontal.getValue() / 100.0;
                        mc.player.motionZ *= horizontal.getValue() / 100.0;
                    }
                }
            }
        }

        if (explosionPacket.getValue()) {
            if (e.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
                if (vertical.getValue().equals(0.0) && horizontal.getValue().equals(0.0)) {
                    e.cancelEvent();
                } else {
                    packet.field_149152_f *= horizontal.getValue().floatValue() / 100.0F;
                    packet.field_149153_g *= vertical.getValue().floatValue() / 100.0F;
                    packet.field_149159_h *= horizontal.getValue().floatValue() / 100.0F;
                }
            }
        }
    }

    public static void strafe(final double d) {
        if (! mc.player.isMoving())
            return;

        final double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * d;
        mc.player.motionZ = Math.cos(yaw) * d;
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
