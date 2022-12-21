package io.space.events;

import com.darkmagician6.eventapi.events.Event;

public final class EventKey implements Event {
    private final int keyCode;

    public EventKey(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
