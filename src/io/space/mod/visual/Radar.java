package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.designer.designerimpl.RadarDesigner;
import io.space.events.Event2D;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;

public final class Radar extends Mod {
    public static final NumberValue scale = new NumberValue("Scale",1,0.1,5.0,0.1);
    public static final NumberValue size = new NumberValue("Size",100,5,500,1);
    public static final BooleanValue pvp = new BooleanValue("PVP",true);

    public Radar() {
        super("Radar",Category.VISUAL);
        registerValues(scale,size,pvp);
    }

    @EventTarget
    public void on2D(Event2D e) {
        RadarDesigner.Instance.draw(e.getPartialTicks(),Wrapper.Instance.getMouseX(),Wrapper.Instance.getMouseY());
    }
}
