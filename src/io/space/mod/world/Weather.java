package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public final class Weather extends Mod {
    private final NumberValue time = new NumberValue("Time",18000.0,0.0,24000.0,1.0);

    private long serverTime;

    public Weather() {
        super("Weather",Category.WORLD);
        registerValues(time);
    }

    @Override
    protected void onDisable() {
        mc.world.setWorldTime(serverTime);

        super.onDisable();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S03PacketTimeUpdate) {
            serverTime = ((S03PacketTimeUpdate) e.getPacket()).getWorldTime();
            e.cancelEvent();
        }
    }

    @EventTarget
    public void onTick(EventTick event) {
        mc.world.setWorldTime(time.getValue().longValue());
    }
}
