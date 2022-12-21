package io.space.mod.visual

import com.darkmagician6.eventapi.EventTarget
import io.space.events.Event2D
import io.space.events.Event3D
import io.space.mod.Mod
import io.space.renderer.font.FontManager
import io.space.utils.RenderUtils
import io.space.value.values.BooleanValue
import io.space.value.values.ColorValue
import io.space.value.values.ModeValue
import io.space.value.values.NumberValue
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.util.BlockPos

class BlockOverlay : Mod("BlockOverlay",Category.VISUAL) {
    private val fontMode = ModeValue("FontMode","Default",arrayOf("Default","Client"))
    private val color = ColorValue("Color",255,255,255)
    private val alpha = NumberValue("Alpha",255,0,255,1)
    private val information = BooleanValue("Information",false)
    private val outline = BooleanValue("Outline",false)

    private var currentBlock : Block? = null
    private var currentBlockPos : BlockPos? = null

    companion object {
        @JvmStatic
        lateinit var Instance : BlockOverlay
    }

    init {
        registerValues(fontMode,color,alpha,information,outline)
        Instance = this
    }

    @EventTarget
    fun on2D(e : Event2D) {
        if (information.value)
            if (currentBlockPos != null && currentBlock != null) {
                val useDefault = fontMode.isCurrentMode("Default")
                val s = currentBlock?.localizedName
                val x = e.width.toDouble() / 2.0 + 10.0
                val y = e.height.toDouble() / 2.0 - 5.0

                RenderUtils.drawRect(
                    x,
                    y,
                    x + (if (useDefault) mc.fontRenderer.getStringWidth(s) else FontManager.default16.getStringWidth(s)) + 2,
                    y + 10.0,
                    RenderUtils.getRGB(0, 0, 0, 50)
                )

                if (useDefault) {
                    mc.fontRenderer.drawStringWithShadow(s, x.toFloat() + 1, y.toFloat() + 1, -1)
                } else FontManager.default16.drawStringWithShadow(s, x + 1, y + 1, -1)
            }
    }

    @EventTarget
    fun on3D(e : Event3D) {
        currentBlockPos = null
        currentBlock = null

        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.blockPos != null) {
                val block = mc.world.getBlock(mc.objectMouseOver.blockPos)
                if (block !is BlockAir) {
                    currentBlockPos = mc.objectMouseOver.blockPos
                    currentBlock = block
                    RenderUtils.drawBlockBox(currentBlockPos,RenderUtils.getRGB(color.red,color.green,color.blue,alpha.value.toInt()),outline.value)
                }
            }
        }
    }
}