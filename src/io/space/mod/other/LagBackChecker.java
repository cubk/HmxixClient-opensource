package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.value.values.BooleanValue;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public final class LagBackChecker extends Mod {
    private final BooleanValue speed = new BooleanValue("Speed",true);
    private final BooleanValue fly = new BooleanValue("Fly",true);
    private final BooleanValue timer = new BooleanValue("Timer",true);

    public LagBackChecker() {
        super("LagBackChecker",Category.OTHER);
        registerValues(speed,fly,timer);
    }

    @EventTarget(value = Priority.HIGHEST)
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            if (speed.getValue()) disableMod("Speed");
            if (fly.getValue()) disableMod("Fly");
            if (timer.getValue()) disableMod("Timer");
        }
    }

    private void disableMod(String modName) {
        final Mod mod = ModManager.Instance.getModFromName(modName);

        if (mod == null) {
            new RuntimeException(modName + " not exist!").printStackTrace();
        } else {
            if (mod.isEnable()) {
                mod.setEnable(false);
                NotificationManager.Instance.addNotification("LagBackChecker","Disable mod " + mod.getRenderName(),Notification.NotificationType.WARNING,4000);
            }
        }
    }
}
