package io.space.renderer.gui;

import by.radioegor146.annotation.Native;
import io.space.network.NetworkClient;
import io.space.network.common.client.CPacketAuth;
import io.space.network.common.server.SPacketAuthResult;
import io.space.network.common.server.SPacketRegisterResult;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.renderer.font.FontManager;
import io.space.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import utils.hodgepodge.object.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GuiLogin extends GuiScreen {
    protected static GuiLogin Instance;
    protected static GuiRegister RInstance;

    protected GuiTextField userNameTextField;
    protected GuiTextField passwordTextField;

    protected String title = "登录";

    public GuiButton loginButton;
    public String statusText = "等待中...";

    public GuiLogin() {
        Instance = this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        final double wMid = width / 2.0;
        final double hMid = height / 2.0;
        RenderUtils.drawBorderedRect(wMid - 120,hMid - 70,wMid + 120,hMid + 70,1,RenderUtils.getRGB(100,255,100),RenderUtils.getRGB(0,0,0,100));
        FontManager.default16.drawStringWithOutline(title,wMid - FontManager.default16.getStringWidth("Login") / 2.0,hMid - 68,-1);
        FontManager.default16.drawStringWithOutline(statusText,wMid - FontManager.default16.getStringWidth(statusText) / 2.0,hMid - 58,-1);

        userNameTextField.drawTextBox();
        passwordTextField.drawTextBox();

        if (!userNameTextField.isFocused() && StringUtils.isNullOrEmpty(userNameTextField.getText())) {
            mc.fontRenderer.drawStringWithShadow("UserName",(float) wMid - 114,(float) hMid - 24,RenderUtils.getRGB(100,100,100));
        }

        if (!passwordTextField.isFocused() && StringUtils.isNullOrEmpty(passwordTextField.getText())) {
            mc.fontRenderer.drawStringWithShadow("Password",(float) wMid - 114,(float) hMid + 6,RenderUtils.getRGB(100,100,100));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    @Native
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (userNameTextField.getText() == null || userNameTextField.getText().isEmpty()) {
                NotificationManager.Instance.addNotification("Login","User name cannot be empty", Notification.NotificationType.WARNING,5000);
                return;
            }

            if (passwordTextField.getText() == null || passwordTextField.getText().isEmpty()) {
                NotificationManager.Instance.addNotification("Login","Password cannot be empty", Notification.NotificationType.WARNING,5000);
                return;
            }

            statusText = "登录中...";
            loginButton.enabled = false;

            try {
                if (NetworkClient.Instance.isOpen()) {
                    NetworkClient.Instance.userName = userNameTextField.getText();
                    NetworkClient.Instance.sendPacket(new CPacketAuth(userNameTextField.getText(), passwordTextField.getText(), getHardwareID()));
                } else {
                    statusText = "Can't send packet: Channel closed";
                }
            } catch (NoSuchAlgorithmException e) {
                statusText = e.getClass().getName() + ':' + e.getMessage();
            }
        } else if (button.id == 1) {
            mc.shutdown();
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiRegister());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        userNameTextField.mouseClicked(mouseX,mouseY,mouseButton);
        passwordTextField.mouseClicked(mouseX,mouseY,mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        userNameTextField.updateCursorCounter();
        passwordTextField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        userNameTextField.textboxKeyTyped(typedChar,keyCode);
        passwordTextField.textboxKeyTyped(typedChar,keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();

        final int wMid = width / 2;
        final int hMid = height / 2;
        buttonList.add(loginButton = new GuiButton(0,wMid - 118,hMid + 48,60,20,"登录"));
        buttonList.add(new GuiButton(1,wMid + 58,hMid + 48,60,20,"退出"));
        buttonList.add(new GuiButton(2,wMid - 29,hMid + 48,60,20,"注册"));

        userNameTextField = new GuiTextField(0,mc.fontRenderer,wMid - 116,hMid - 30,233,20);
        passwordTextField = new GuiTextField(1,mc.fontRenderer,wMid - 116,hMid,233,20);
    }

    @Native
    protected static String getHardwareID() throws NoSuchAlgorithmException {
        return sha1(System.getenv("COMPUTERNAME") +
                System.getProperty("os.name") +
                System.getenv("PROCESSOR_LEVEL") +
                System.getenv("PROCESSOR_IDENTIFIER") +
                System.getenv("NUMBER_OF_PROCESSORS"));
    }

    @Native
    private static String sha1(String s) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        final byte[] digested = digest.digest(s.getBytes(StandardCharsets.UTF_8));

        final StringBuilder builder = new StringBuilder();

        for (byte b : digested) {
            builder.append(Integer.toHexString(b & 0xFF));
        }

        return builder.toString();
    }

    @Native
    public static void processAuthResult(SPacketAuthResult packet) {
        if (Instance == null) return;

        Instance.statusText = packet.getReason();
        NetworkClient.Instance.authResult = packet.getResult();

        if (packet.getResult() % 2 == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
        }

        Instance.loginButton.enabled = true;
    }

    @Native
    public static void processRegisterResult(SPacketRegisterResult packet) {
        if (RInstance == null) return;

        RInstance.statusText = packet.getResult();
        RInstance.loginButton.enabled = true;
    }

    public static void processError(Throwable cause) {
        final String s = cause.getClass().getName() + ':' + cause.getMessage();

        if (Instance != null) {
            Instance.statusText = s;
        }

        if (RInstance != null) {
            RInstance.statusText = s;
        }
    }
    public static void processChannelInactive() {
        if (Instance != null) {
            Instance.statusText = "Channel closed!";
        }

        if (RInstance != null) {
            RInstance.statusText = "Channel closed!";
        }
    }
}
