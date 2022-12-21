package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import utils.hodgepodge.object.time.TimerUtils;

import java.util.LinkedList;

public final class PingSpoof extends Mod {
    private final NumberValue ping = new NumberValue("Ping",1000,0,10000,1);

    private final LinkedList<C00PacketKeepAlive> packets = new LinkedList<>();
    private final TimerUtils timerUtils = new TimerUtils(true);

    public PingSpoof() {
        super("PingSpoof",Category.OTHER);
        registerValues(ping);
    }

    @Override
    protected void onDisable() {
        pollAll();
        timerUtils.reset();

        super.onDisable();
    }

    @Override
    protected void onEnable() {
        timerUtils.reset();
        super.onEnable();
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (timerUtils.hasReached(ping.getValue())) {
            pollAll();
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (mc.isSingleplayer()) return;

        if (e.getPacket() instanceof C00PacketKeepAlive) {
            packets.add((C00PacketKeepAlive) e.getPacket());
        }
    }

    private void pollAll() {
        if (!packets.isEmpty()) {
            C00PacketKeepAlive packet;

            while ((packet = packets.poll()) != null) {
                mc.getNetHandler().sendPacketNoEvent(packet);
            }
        }
    }
}
