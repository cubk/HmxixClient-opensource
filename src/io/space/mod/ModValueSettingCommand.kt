package io.space.mod

import io.space.Wrapper.sendMessage
import io.space.command.Command
import io.space.utils.RenderUtils
import io.space.value.ValueType.*
import io.space.value.values.*
import java.awt.Color

private class ModValueSettingCommand(private val mod : Mod) : Command(mod.modName) {
    init {
        hideHelp = true
    }

    @Suppress("DuplicatedCode")
    override fun execute(args: Array<String>) {
        super.execute(args)

        if (args.size < 2) {
            sendMessage("-${mod.modName} <Value Name> <Args>")
            return
        }

        val valueName = args[0]
        val value = mod.values.find { it.valueName.equals(valueName) }

        if (value == null) {
            sendMessage("未知Value $valueName")
        } else {
            if (value.valueType == null) { // 这不应该发生 是不是写错了什么?
                sendMessage("Value type is null!!! value name: $valueName value class name : ${value.javaClass.name}")
                return
            }

            if (value.valueType == NULL) { // 预防空指针从你我做起
                sendMessage("ValueType为NULL 是不是某个value不存在呢 value name: $valueName value class name : ${value.javaClass.name}")
                return
            }

            when(value) {
                is BooleanValue -> {
                    when(args[1].lowercase()) {
                        "false" -> {
                            value.setValueDirect(false)
                            sendMessage("成功将${valueName}的值设置为:${args[1]}")
                        }
                        "true" -> {
                            value.setValueDirect(true)
                            sendMessage("成功将${valueName}的值设置为:${args[1]}")
                        }
                        else -> {
                            sendMessage("-${mod.modName} $valueName <true or false>")
                        }
                    }
                }
                is NumberValue -> {
                    val d = args[1].toDoubleOrNull()

                    if (d == null) {
                        sendMessage("${args[1]} 不是一个数字!")
                        return
                    }

                    value.setValueDirect(d)
                    sendMessage("成功将${valueName}的值设置为:${args[1]}")
                }
                is ModeValue -> {
                    if (args[1].contains(":")) {
                        sendMessage("不可含有 ':'")
                        return
                    }

                    value.setValueDirect(args[1])
                    sendMessage("成功将${valueName}的值设置为:${args[1]}")
                }
                is TextValue -> {
                    if (args[1].contains(":")) {
                        sendMessage("不可含有 ':'")
                        return
                    }

                    value.setValueDirect(args[1])
                    value.guiTextField.text = args[1]
                    sendMessage("成功将${valueName}的值设置为:${args[1]}")
                }
                is ColorValue -> {
                    when (args.size) {
                        2 -> {
                            val rgb = args[1].toIntOrNull()

                            if (rgb == null) {
                                sendMessage("${args[1]} 不是一个Integer!")
                                return
                            }

                            val ints = RenderUtils.splitRGB(rgb)
                            val hsb = Color.RGBtoHSB(ints[0], ints[1], ints[2], null)

                            value.hue = hsb[0]
                            value.saturation = hsb[1]
                            value.brightness = hsb[2]

                            value.setValueDirect(rgb)

                            sendMessage("成功将${valueName}的RGB设置为:${args[1]}")
                        }
                        5 -> {
                            when(args[1].lowercase()) {
                                "rgb" -> {
                                    val r = args[2].toIntOrNull()
                                    val g = args[3].toIntOrNull()
                                    val b = args[4].toIntOrNull()

                                    if (r == null) {
                                        sendMessage("${args[2]} 不是一个Integer!")
                                        return
                                    }

                                    if (g == null) {
                                        sendMessage("${args[3]} 不是一个Integer!")
                                        return
                                    }

                                    if (b == null) {
                                        sendMessage("${args[4]} 不是一个Integer!")
                                        return
                                    }

                                    if (r > 255 || r < 0) {
                                        sendMessage("${args[2]} 太大或太小了 请在0-255内设置!")
                                        return
                                    }

                                    if (g > 255 || g < 0) {
                                        sendMessage("${args[3]} 太大或太小了 请在0-255内设置!")
                                        return
                                    }

                                    if (b > 255 || b < 0) {
                                        sendMessage("${args[4]} 太大或太小了 请在0-255内设置!")
                                        return
                                    }

                                    val hsb = Color.RGBtoHSB(r,g,b, null)

                                    value.hue = hsb[0]
                                    value.saturation = hsb[1]
                                    value.brightness = hsb[2]

                                    value.setValueDirect(RenderUtils.getRGB(r,g,b))
                                    sendMessage("成功将${valueName}的RGB设置为:${args[2]},${args[3]},${args[4]}")
                                }
                                "hsb" -> {
                                    val hue = args[2].toFloatOrNull()
                                    val saturation = args[3].toFloatOrNull()
                                    val brightness = args[4].toFloatOrNull()

                                    if (hue == null) {
                                        sendMessage("${args[2]} 不是一个数字!")
                                        return
                                    }

                                    if (saturation == null) {
                                        sendMessage("${args[3]} 不是一个数字!")
                                        return
                                    }

                                    if (brightness == null) {
                                        sendMessage("${args[4]} 不是一个数字!")
                                        return
                                    }

                                    if (hue > 1 || hue < 0) {
                                        sendMessage("${args[2]} 太大或太小了 请在0-1内设置!")
                                        return
                                    }

                                    if (saturation > 1 || saturation < 0) {
                                        sendMessage("${args[3]} 太大或太小了 请在0-1内设置!")
                                        return
                                    }

                                    if (brightness > 1 || brightness < 0) {
                                        sendMessage("${args[4]} 太大或太小了 请在0-1内设置!")
                                        return
                                    }

                                    value.hue = hue
                                    value.saturation = saturation
                                    value.brightness = brightness

                                    value.setValueDirect(RenderUtils.getRGB(Color.HSBtoRGB(hue,saturation,brightness)))
                                    sendMessage("成功将${valueName}的HSB设置为:${args[2]},${args[3]},${args[4]}")
                                }
                            }
                        }
                        else -> {
                            sendMessage("-${mod.modName} $valueName <RGB>")
                            sendMessage("-${mod.modName} $valueName rgb <Red> <Green> <Blue>")
                            sendMessage("-${mod.modName} $valueName hsb <HUE> <Saturation> <Brightness>")
                        }
                    }
                }
            }
        }
    }
}