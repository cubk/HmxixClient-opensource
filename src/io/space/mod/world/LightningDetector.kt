package io.space.mod.world

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventPacket
import io.space.mod.Mod
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting

class LightningDetector : Mod("LightningDetector",Category.WORLD) {
    @EventTarget
    fun onPacket(e : EventPacket) {
        val packet = e.packet

        if (packet is S2CPacketSpawnGlobalEntity) {
            if (packet.type == 1) {
                val x = (packet.x / 32.0).toInt()
                val y = (packet.y / 32.0).toInt()
                val z = (packet.z / 32.0).toInt()

                val chatComponentText = ChatComponentText("${EnumChatFormatting.AQUA}[LightningDetector] ${EnumChatFormatting.WHITE}距离${mc.player.getDistance(x.toDouble(),y.toDouble(),z.toDouble()).toInt()}m检测到闪电 x:$x y:$y z:$z ")
                val clickEventChatComponent = ChatComponentText("${EnumChatFormatting.WHITE} [${EnumChatFormatting.YELLOW}点我TP${EnumChatFormatting.WHITE}]")

                val chatStyle = clickEventChatComponent.chatStyle
                chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "-tp $x $y $z")
                chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("点我TP啦!~"))

                chatComponentText.appendSibling(clickEventChatComponent)

                mc.player.addChatMessage(chatComponentText)
            }
        }
    }
}