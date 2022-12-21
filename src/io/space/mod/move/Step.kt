package io.space.mod.move

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventPostStep
import io.space.events.EventPreStep
import io.space.mod.Mod
import io.space.value.values.ModeValue
import io.space.value.values.NumberValue

class Step : Mod("Step",Category.MOVE) {
    private val mode = ModeValue("Mode","Vanilla",arrayOf("Vanilla"))
    private val stepHeight = NumberValue("StepHeight",1.0,0.0,10.0,0.01)

    init {
        registerValues(mode,stepHeight)
    }

    private var oldStepHeight = 0f

    @EventTarget
    fun onPreStep(e : EventPreStep) {
        if (mode.isCurrentMode("Vanilla")) {
            oldStepHeight = mc.player.stepHeight;

            mc.player.stepHeight = stepHeight.value.toFloat()
            mc.timer.timerSpeed = 0.01f
        }
    }

    @EventTarget
    fun onPostStep(e : EventPostStep) {
        if (mode.isCurrentMode("Vanilla")) {
            mc.player.stepHeight = oldStepHeight
            mc.timer.timerSpeed = 1.0f
        }
    }
}