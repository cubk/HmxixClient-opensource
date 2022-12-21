package io.space.mod.move;

import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public final class Clip extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Packet",new String[]{"TP","Packet"});
    private final NumberValue horizontal = new NumberValue("Horizontal",1.0,-10.0, 10.0, 0.1);
    private final NumberValue vertical = new NumberValue("Vertical",1.0, -10.0, 10, 0.1);

    public Clip() {
        super("Clip",Category.MOVE);
        registerValues(mode,horizontal,vertical);
    }
    
    @Override
    public void onEnable() {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        double x = -sin(yaw) * horizontal.getValue();
        double z = cos(yaw) * horizontal.getValue();
        if(mode.isCurrentMode("TP")) {
            mc.player.setPosition(mc.player.posX + x,mc.player.posY + vertical.getValue(), mc.player.posZ + z);
        } else if(mode.isCurrentMode("Packet")) {
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX,mc.player.posY,mc.player.posZ, true));
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(0.5,0,0.5, true));
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX,mc.player.posY,mc.player.posZ, true));
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX + x,mc.player.posY + vertical.getValue(),mc.player.posZ + z, true));
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(0.5,0,0.5, true));
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX + 0.5,mc.player.posY,mc.player.posZ + 0.5, true));
            mc.player.setPosition(mc.player.posX + -sin(yaw) * 0.04,mc.player.posY,mc.player.posZ + cos(yaw) * 0.04);
        }

        setEnable(false);
    }
}
