package io.space.mod.move

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventCollideWithBlock
import io.space.events.EventPreUpdate
import io.space.mod.Mod
import io.space.utils.PlayerUtils
import io.space.value.values.ModeValue
import io.space.value.values.NumberValue
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

class Jesus : Mod("Jesus",Category.MOVE) {
    private val mode = ModeValue("Mode","Solid", arrayOf("Solid","Dolphin","Motion"))
    private val motionY = NumberValue("MotionY",1.0,0.0,10.0,0.01)

    private var dolphinWasWater = false
    private var dolphinTicks = 0

    init {
        registerValues(mode,motionY)
    }

    override fun onEnable() {
        super.onEnable()
        dolphinWasWater = false
        dolphinTicks = 0
    }

    @EventTarget
    fun onPreUpdate(e : EventPreUpdate) {
        if (mode.isCurrentMode("Solid")) {
            if (PlayerUtils.isOnLiquid() && !mc.player.isInLiquid && !mc.gameSettings.keyBindSneak.isKeyDown && mc.player.fallDistance < 3.0) {
                e.y += if (mc.player.ticksExisted % 2 == 0) 0.1 else -0.1
                e.isOnGround = true
            }
        } else if (mode.isCurrentMode("Dolphin")) {
            var should = false
            val x = mc.player.posX
            val y = mc.player.posY
            val z = mc.player.posZ
            val pos = listOf(
                    BlockPos(x + 0.3, y, z + 0.3),
                    BlockPos(x - 0.3, y, z + 0.3),
                    BlockPos(x + 0.3, y, z - 0.3),
                    BlockPos(x - 0.3, y, z - 0.3)
                )

            for (po in pos) {
                if (mc.world.getBlockState(po).block !is BlockLiquid) continue
                if (mc.world.getBlockState(po).properties[BlockLiquid.LEVEL] is Int) {
                    if (mc.world.getBlockState(po).properties[BlockLiquid.LEVEL] as Int <= 4) {
                        should = true
                    }
                }
            }

            if (mc.player.isInWater && !mc.player.isSneaking && should) {
                mc.player.motionY = 0.09
            }

            if (mc.player.onGround || mc.player.isOnLadder) {
                dolphinWasWater = false
            }

            if (mc.player.motionY > 0.0 && dolphinWasWater) {
                if (mc.player.motionY <= 0.11) {
                    mc.player.motionY *= 1.2671
                }
                mc.player.motionY += 0.05172
            }

            if (mc.player.isInLiquid && !mc.player.isSneaking) {
                if (dolphinTicks < 3) {
                    mc.player.motionY = 0.13
                    dolphinTicks++
                    dolphinWasWater = false
                } else {
                    mc.player.motionY = 0.5
                    dolphinTicks = 0
                    dolphinWasWater = true
                }
            }
        } else if (mode.isCurrentMode("Motion")) {
            if (mc.player.isInLiquid && !mc.gameSettings.keyBindSneak.isKeyDown) {
                mc.player.motionY = motionY.value
            }
        }
    }

    @EventTarget
    fun onCollideWithBlock(e : EventCollideWithBlock) {
        if (mode.isCurrentMode("Solid")) {
            if (e.block is BlockLiquid && PlayerUtils.isOnLiquid() && !mc.player.isInLiquid && !mc.gameSettings.keyBindSneak.isKeyDown && mc.player.fallDistance < 3.0) {
                e.boundingBox = AxisAlignedBB(
                    e.blockPos.x.toDouble(),
                    e.blockPos.y.toDouble(),
                    e.blockPos.z.toDouble(), e.blockPos.x + 1.0, e.blockPos.y + 1.0, e.blockPos.z + 1.0
                )
            }
        }
    }
}