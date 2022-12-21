package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.utils.pathfinder.PathFinder;
import io.space.utils.pathfinder.SigmaVec3;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.Collections;
import java.util.List;

public final class FreeCamHit extends Mod {
    private Vec3 oldPosition;

    public FreeCamHit() {
        super("FreeCamHit", Category.PLAYER);
    }

    @EventTarget
    public void onTick(EventTick e) {
        mc.player.motionY = 0.0D;
        if (!mc.player.isMoving()) {
            mc.player.motionX = mc.player.motionZ = 0.0D;
        } else {
            MoveUtils.setSpeed(1);
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -1;
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = 1;
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.isSend() && e.getPacket() instanceof C03PacketPlayer) {
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(true));
            e.cancelEvent();
        }

        if (e.isSend() && e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();

            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                e.cancelEvent();

                final SigmaVec3 topFrom = new SigmaVec3(oldPosition.xCoord,oldPosition.yCoord,oldPosition.zCoord);
                final SigmaVec3 to = new SigmaVec3(packet.getEntityFromWorld(mc.world).posX,packet.getEntityFromWorld(mc.world).posY,packet.getEntityFromWorld(mc.world).posZ);
                final List<SigmaVec3> path = PathFinder.computePath(topFrom, to);

                for (SigmaVec3 vec3 : path) {
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true));
                }

                mc.getNetHandler().sendPacketNoEvent(new C02PacketUseEntity(packet.getEntityFromWorld(mc.world), C02PacketUseEntity.Action.ATTACK));

                Collections.reverse(path);

                for (SigmaVec3 vec3 : path) {
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true));
                }
            }
        }
    }

    @Override
    protected void onEnable() {
        oldPosition = mc.player.getPositionVector();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        if (oldPosition != null) {
            mc.player.setPositionAndUpdate(oldPosition.xCoord,oldPosition.yCoord,oldPosition.zCoord);
        }
        super.onDisable();
    }
}
