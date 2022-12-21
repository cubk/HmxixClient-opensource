package io.space.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.EntityLivingBase;

public final class EventLivingUpdate implements Event {
    private final EntityLivingBase entity;

    public EventLivingUpdate(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
