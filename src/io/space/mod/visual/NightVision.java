package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public final class NightVision extends Mod {
    public static final ModeValue mode = new ModeValue("Mode","Normal",new String[]{"Normal","Potion"});
    public static final NumberValue brightness = new NumberValue("Brightness",1.0,0.0,1.0,0.01);

    public static NightVision Instance;

    public NightVision() {
        super("NightVision",Category.VISUAL);
        registerValues(mode,brightness);
        Instance = this;
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mode.isCurrentMode("Potion")) {
            mc.player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 6420, 1));
        }
    }

    @Override
    public void onDisable() {
        if (mode.isCurrentMode("Potion")) {
            mc.player.removePotionEffect(Potion.nightVision.id);
        }
    }
}
