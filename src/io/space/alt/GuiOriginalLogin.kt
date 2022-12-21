package io.space.alt

import io.space.Wrapper
import io.space.alt.altimpl.OriginalAlt
import io.space.notification.Notification
import io.space.notification.NotificationManager
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.Session
import java.util.*

class GuiOriginalLogin(private val parentScreen : GuiScreen,private val direct : Boolean) : GuiScreen() {
    private lateinit var userName : GuiTextField
    private lateinit var accessToken : GuiTextField
    private lateinit var uuid : GuiTextField
    private lateinit var legacyButton : GuiButton
    private lateinit var mojangButton : GuiButton

    private var selectedType = "mojang"
    private var status = "${EnumChatFormatting.YELLOW}等待中..."

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        val x = width.toFloat() / 2f
        val y = height.toFloat() / 2f

        mc.fontRenderer.drawCenteredStringWithShadow(status,x,y - 50f,-1)

        userName.drawTextBox()
        accessToken.drawTextBox()
        uuid.drawTextBox()

        if (!userName.isFocused && userName.text.isNullOrEmpty()) {
            mc.fontRenderer.drawStringWithShadow("${EnumChatFormatting.DARK_GRAY}${EnumChatFormatting.ITALIC}UserName",x - 77f,y - 30f,-1)
        }

        if (!accessToken.isFocused && accessToken.text.isNullOrEmpty()) {
            mc.fontRenderer.drawStringWithShadow("${EnumChatFormatting.DARK_GRAY}${EnumChatFormatting.ITALIC}AccessToken",x - 77f,y - 5f,-1)
        }

        if (!uuid.isFocused && uuid.text.isNullOrEmpty()) {
            mc.fontRenderer.drawStringWithShadow("${EnumChatFormatting.DARK_GRAY}${EnumChatFormatting.ITALIC}UUID",x - 77f,y + 20f,-1)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)

        userName.textboxKeyTyped(typedChar,keyCode)
        accessToken.textboxKeyTyped(typedChar,keyCode)
        uuid.textboxKeyTyped(typedChar,keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        userName.mouseClicked(mouseX,mouseY,mouseButton)
        accessToken.mouseClicked(mouseX,mouseY,mouseButton)
        uuid.mouseClicked(mouseX,mouseY,mouseButton)
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)

        when(button?.id) {
            0 -> {
                mc.displayGuiScreen(parentScreen)
            }
            1 -> {
                if (direct) {
                    mc.session = Session(userName.text, uuid.text, accessToken.text, selectedType)
                    status = "${EnumChatFormatting.GREEN}登录成功! Type:$selectedType UserName:${userName.text} AccessToken:${accessToken.text} UUID:${uuid.text}"
                } else {
                    AltManager.Instance.addAlt(OriginalAlt(userName.text,accessToken.text,uuid.text,selectedType))
                    status = "${EnumChatFormatting.GREEN}添加成功! Type:$selectedType UserName:${userName.text} AccessToken:${accessToken.text} UUID:${uuid.text}"
                }
            }
            2 -> {
                selectedType = "legacy"
                legacyButton.enabled = false
                mojangButton.enabled = true
            }
            3 -> {
                selectedType = "mojang"
                legacyButton.enabled = true
                mojangButton.enabled = false
            }
            4 -> {
                uuid.text = UUID.randomUUID().toString().replace("-","")
            }
            5 -> {
                NotificationManager.Instance.addNotification("OriginalLogin","你的用户名为:${mc.session.username}",Notification.NotificationType.INFO,15000)
                NotificationManager.Instance.addNotification("OriginalLogin","你的AccessToken为:${mc.session.token}",Notification.NotificationType.INFO,15000)
                NotificationManager.Instance.addNotification("OriginalLogin","你的UUID为:${mc.session.playerID}",Notification.NotificationType.INFO,15000)

                Wrapper.Instance.logger.info("OriginalLogin: UserName:${mc.session.username}")
                Wrapper.Instance.logger.info("OriginalLogin: Token:${mc.session.token}")
                Wrapper.Instance.logger.info("OriginalLogin: UUID:${mc.session.playerID}")
            }
        }
    }

    override fun initGui() {
        super.initGui()

        val x = width / 2
        val y = height / 2

        userName = GuiTextField(0,mc.fontRenderer,x - 80,y - 35,160,20)
        userName.maxStringLength = 500

        accessToken = GuiTextField(1,mc.fontRenderer,x - 80,y - 10,160,20)
        accessToken.maxStringLength = 500

        uuid = GuiTextField(2,mc.fontRenderer,x - 80,y + 15,160,20)
        uuid.maxStringLength = 500

        buttonList.add(GuiButton(0,x + 40,y + 45,40,20,"返回"))
        buttonList.add(GuiButton(1,x - 80,y + 45,40,20,"登录"))

        legacyButton = GuiButton(2,x - 125,y - 35,40,20,"Legacy")
        buttonList.add(legacyButton)

        mojangButton = GuiButton(3,x - 125,y - 10,40,20,"Mojang")
        mojangButton.enabled = false
        buttonList.add(mojangButton)

        buttonList.add(GuiButton(4,x - 30,y + 45,50,20,"随机UUID"))

        buttonList.add(GuiButton(5,x - 80,y + 70,160,20,"获得当前信息"))
    }
}