package io.space.object.teleport.teleports;

import io.space.object.teleport.TeleportToYou;
import io.space.utils.pathfinder.SigmaVec3;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class VanillaTeleport extends TeleportToYou {
    public VanillaTeleport(Minecraft mc, double x, double y, double z) {
        super(mc, x, y, z);
    }

    @Override
    public void teleportToXYZ() {
        for (SigmaVec3 vec3 : findPath()) {
            mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(), vec3.getZ(),true));
            mc.player.setPositionAndUpdate(x,y,z);
        }
    }
}
