package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import net.minecraft.network.play.server.S02PacketChat;

public final class Log4JPatcher extends Mod {
    public Log4JPatcher() {
        super("Log4JPatcher",Category.OTHER);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet = (S02PacketChat) e.getPacket();
            final String formattedText = packet.getChatComponent().getFormattedText();

            if (formattedText.contains("${jndi:")) {
                NotificationManager.Instance.addNotification("Log4JPatcher","成功返回消息:" + formattedText, Notification.NotificationType.INFO,5000);
                e.cancelEvent();
            }
        }
    }
}
