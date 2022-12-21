package io.space.mod.fight

import com.darkmagician6.eventapi.EventTarget
import io.space.`object`.CPSDelay
import io.space.events.EventTick
import io.space.mod.Mod
import io.space.value.values.NumberValue

class Trigger : Mod("Trigger",Category.FIGHT) {
    private val cps = NumberValue("CPS",6.0,1.0,20.0,1.0)

    private val cpsDelay = CPSDelay()

    init {
        registerValues(cps)
    }

    @EventTarget
    fun onTick(e : EventTick) {
        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.entityHit != null) {
                if (cpsDelay.shouldAttack(cps.value.toInt())) {
                    mc.clickMouse()
                }
            }
        }
    }
}