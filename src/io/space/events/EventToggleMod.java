package io.space.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import io.space.mod.Mod;

public final class EventToggleMod extends EventCancellable {
    private final Mod mod;
    private final boolean preEnable,postEnable;

    public EventToggleMod(Mod mod, boolean preEnable, boolean postEnable) {
        this.mod = mod;
        this.preEnable = preEnable;
        this.postEnable = postEnable;
    }
}
