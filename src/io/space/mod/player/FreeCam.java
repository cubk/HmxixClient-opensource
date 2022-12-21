package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.events.EventMove;
import io.space.events.EventPacket;
import io.space.events.EventPreUpdate;
import io.space.events.EventWorldLoad;
import io.space.mod.Mod;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.MoveUtils;
import io.space.utils.PlayerUtils;
import io.space.value.values.NumberValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public final class FreeCam extends Mod {
    private final NumberValue speed = new NumberValue("Speed",1.0, 1.0, 10.0, 1);
    private final NumberValue motionSpeed = new NumberValue("MotionSpeed",1.0, 1.0, 10.0, 1);

    public static FreeCam Instance;

    private EntityOtherPlayerMP playerMP;

    private double oldPosX,oldPosY,oldPosZ;
    private float oldYaw,oldPitch;
    private boolean oldOnGround;

    public FreeCam() {
        super("FreeCam",Category.PLAYER);
        registerValues(speed,motionSpeed);

        Instance = this;
    }

    @Override
    protected void onEnable() {
        if (mc.world != null) {
            playerMP = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
            playerMP.clonePlayer(mc.player, true);
            playerMP.setLocationAndAngles(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
            playerMP.rotationYawHead = mc.player.rotationYawHead;
            playerMP.setEntityId(-1337);
            playerMP.setSneaking(mc.player.isSneaking());
            mc.world.addEntityToWorld(playerMP.getEntityId(), playerMP);
            oldPosX = mc.player.posX;
            oldPosY = mc.player.posY;
            oldPosZ = mc.player.posZ;
            oldYaw = mc.player.rotationYaw;
            oldPitch = mc.player.rotationPitch;
            oldOnGround = mc.player.onGround;

            mc.player.noClip = true;
        }
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        mc.world.removeEntityFromWorld(playerMP.getEntityId());
        mc.player.motionX = mc.player.motionZ = 0.0;
        mc.player.noClip = false;
        mc.player.onGround = oldOnGround;
        mc.player.setPositionAndRotation(oldPosX,oldPosY,oldPosZ,oldYaw,oldPitch);
        mc.player.motionY = mc.player.motionX = mc.player.motionZ = 0;
        super.onDisable();
    }

    @EventTarget(value = Priority.LOWEST)
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            e.cancelEvent();
        }
        if (e.getPacket() instanceof C0APacketAnimation) {
            e.cancelEvent();
        }
        if (e.getPacket() instanceof C02PacketUseEntity) {
            e.cancelEvent();
        }
        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            e.cancelEvent();
        }
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            e.cancelEvent();
        }
        if (e.getPacket() instanceof C0BPacketEntityAction) {
            e.cancelEvent();
        }
    }

    @EventTarget
    private void onUpdate(EventPreUpdate e) {
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(oldOnGround));
        mc.player.onGround = PlayerUtils.isOnGround(0.001);
        mc.player.motionY = 0.0D;
        if (!mc.player.isMoving()) {
            mc.player.motionX = mc.player.motionZ = 0.0D;
        } else {
            MoveUtils.setSpeed(speed.getValue());
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -motionSpeed.getValue();
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = motionSpeed.getValue();
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        mc.player.noClip = true;
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        NotificationManager.Instance.addNotification("FreeCam","检测到世界加载 关闭FreeCam!", Notification.NotificationType.WARNING,5000);
        setEnable(false);
    }
}
