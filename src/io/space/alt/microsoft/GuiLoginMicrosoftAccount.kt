package io.space.alt.microsoft

import io.space.renderer.font.FontManager
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen

abstract class GuiLoginMicrosoftAccount(private val parentScreen : GuiScreen) : GuiScreen() {
    private val thread = object : Thread("Microsoft Login Thread") {
        private var auth : MicrosoftAuth? = null

        override fun run() {
            try {
                auth = MicrosoftAuth(
                    object : MicrosoftAuth.Callback<MicrosoftAuth.Data> {
                        override fun call(obj: MicrosoftAuth.Data) {
                            onLogin(obj)
                        }
                    },
                    object : MicrosoftAuth.Callback<String> {
                        override fun call(obj: String) {
                            status = obj
                        }
                    }
                )
            } catch (e : Throwable) {
                e.printStackTrace()

                status = "${e.javaClass.name}:${e.message}"
            }
        }

        fun close() {
            auth?.close()

            interrupt()
        }
    }

    private var threadStarted = false
    private var status = "登录中"

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        FontManager.default16.drawCenteredStringWithShadow(status,width / 2.0,height / 2.0 - FontManager.default16.height,-1)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen)
        }
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        thread.close()
    }

    override fun initGui() {
        buttonList.clear()

        buttonList.add(GuiButton(0,width / 2 - 100,height / 2 + 50,"Back"))

        if (!threadStarted) {
            thread.isDaemon = true
            thread.start()

            threadStarted = true
        }
    }

    protected abstract fun onLogin(data: MicrosoftAuth.Data)
}