package io.space.chatcode

import net.minecraft.client.Minecraft
import java.text.SimpleDateFormat
import java.util.*

class ChatCodeManager {
    companion object {
        @JvmStatic
        lateinit var Instance: ChatCodeManager
    }

    private val codeMap = HashMap<String,ChatCode>()

    init {
        registerCode("%time%", object : ChatCode {
            override fun getValue(): String {
                return SimpleDateFormat("HH:mm").format(Date())
            }
        })

        registerCode("%playername%", object : ChatCode {
            override fun getValue(): String {
                return if (Minecraft.getMinecraft().player == null) "null" else Minecraft.getMinecraft().player.name
            }
        })
    }

    fun replace(code : String) : String {
        var ret = code

        codeMap.forEach(action = {
            if (ret.contains(it.key))
                ret = ret.replace(it.key,it.value.getValue())
        })

        return ret
    }

    fun registerCode(key : String,chatCode: ChatCode) {
        codeMap[key] = chatCode
    }
}