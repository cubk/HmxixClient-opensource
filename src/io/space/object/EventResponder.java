package io.space.object;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.Wrapper;
import io.space.command.commands.AutoWalkCommand;
import io.space.events.EventPostUpdateMovementInput;
import io.space.events.EventPreUpdate;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.RotationUtils;
import io.space.utils.pathfinder.SigmaVec3;
import net.minecraft.client.Minecraft;
import utils.hodgepodge.object.StringUtils;

import java.util.List;

public final class EventResponder {
    public static EventResponder Instance;

    private final Minecraft mc = Minecraft.getMinecraft();

    public EventResponder() {
        EventManager.register(this);
    }

    @EventTarget(value = Priority.LOWEST)
    public void onPreUpdate(EventPreUpdate e) {
        Wrapper.Instance.setUpdateYaw(e.getYaw());
        Wrapper.Instance.setUpdatePitch(e.getPitch());

        if (AutoWalkCommand.needWalk) {
            final List<SigmaVec3> path = AutoWalkCommand.path;
            final SigmaVec3 vec3 = path.get(AutoWalkCommand.index);
            final SigmaVec3 lastVec3 = path.get(path.size() - 1);

            if ((int) mc.player.posX == (int) lastVec3.getX() && (int) mc.player.posZ == (int) lastVec3.getZ()) {
                AutoWalkCommand.needWalk = false;
                AutoWalkCommand.path = null;
                AutoWalkCommand.index = 0;

                NotificationManager.Instance.addNotification("AutoWalk","已到达目标地点",Notification.NotificationType.INFO,5000);
            } else {
                if ((int) mc.player.posX == (int) vec3.getX() && (int) mc.player.posZ == (int) vec3.getZ()) {
                    NotificationManager.Instance.addNotification("AutoWalk", StringUtils.compileString("下一个目标 X:{0} Y:{1} Z:{2} Index:{3}",vec3.getX(),vec3.getY(),vec3.getZ(),AutoWalkCommand.index),Notification.NotificationType.INFO,2000);
                    AutoWalkCommand.index++;
                }

                final float[] rotation = RotationUtils.getRotationFromPosition(vec3.getX(),vec3.getZ(), 0);
                mc.player.rotationYaw = rotation[0];
            }
        }
    }

    @EventTarget
    public void onMovementUpdate(EventPostUpdateMovementInput e) {
        if (AutoWalkCommand.needWalk) {
            mc.player.movementInput.moveForward = 1;

            if (mc.player.isCollidedHorizontally && mc.player.onGround) {
                mc.player.movementInput.jump = true;
            }
        }
    }
}
