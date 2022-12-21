package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.designer.designerimpl.PacketGraphDesigner;
import io.space.events.Event2D;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.value.values.ColorValue;
import io.space.value.values.NumberValue;

public final class PacketGraph extends Mod {
    public static final NumberValue updateTime = new NumberValue("UpdateTime",20,0,1000,1);
    public static final ColorValue color = new ColorValue("Color",1,1,1);

    public PacketGraph() {
        super("PacketGraph",Category.VISUAL);
        registerValues(updateTime,color);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        PacketGraphDesigner.Instance.onPacket(e.isSend());
    }

    @EventTarget
    public void on2D(Event2D e) {
        PacketGraphDesigner.Instance.draw(e.getPartialTicks(), Wrapper.Instance.getMouseX(), Wrapper.Instance.getMouseY());
    }
}
