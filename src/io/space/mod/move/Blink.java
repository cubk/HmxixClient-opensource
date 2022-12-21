package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;
import io.space.events.EventPacket;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import utils.hodgepodge.object.time.TimerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Blink extends Mod {
    private final NumberValue delay = new NumberValue("Delay",0.0, 0.0, 3000.0, 1.0);
    private final BooleanValue spawn = new BooleanValue("Spawn",true);

    private EntityOtherPlayerMP blinkEntity;
    private final ArrayList<Packet<?>> packetList = new ArrayList<>();
    private boolean blinking = false;
    private final TimerUtils timerUtil = new TimerUtils(true);

    public Blink() {
        super("Blink",Category.MOVE);
        registerValues(delay,spawn);
    }

    @Override
    public void onEnable() {
        startBlink();
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (delay.getValue() != 0.0 && timerUtil.hasReached(delay.getValue().longValue()) && blinking) {
            try {
                blinking = false;
                stopBlink();
                startBlink();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (blinking && (event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C08PacketPlayerBlockPlacement)) {
            packetList.add(event.getPacket());
            event.cancelEvent();
        }
    }

    @Override
    public void onDisable() {
        if (blinking) {
            stopBlink();
        }
    }

    private void startBlink() {
        if (spawn.getValue()) {
            blinkEntity = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.randomUUID(), "Blink"));
            blinkEntity.inventory = mc.player.inventory;
            blinkEntity.inventoryContainer = mc.player.inventoryContainer;
            blinkEntity.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
            blinkEntity.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(blinkEntity.getEntityId(), blinkEntity);
        }

        blinking = true;
    }

    private void stopBlink() {
        for (Packet<?> packet : packetList) {
            mc.getNetHandler().sendPacketNoEvent(packet);
        }

        packetList.clear();

        if (spawn.getValue()) {
            mc.world.removeEntityFromWorld(blinkEntity.getEntityId());
        }

        blinking = false;
    }
}
