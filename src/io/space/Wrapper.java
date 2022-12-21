package io.space;

import by.radioegor146.annotation.Native;
import io.space.alt.AltManager;
import io.space.cape.CapeManager;
import io.space.chatcode.ChatCodeManager;
import io.space.command.CommandManager;
import io.space.config.ConfigManager;
import io.space.config.NormalConfig;
import io.space.designer.DesignerManager;
import io.space.global.GlobalSetting;
import io.space.mod.ModManager;
import io.space.notification.NotificationManager;
import io.space.object.EventResponder;
import io.space.renderer.font.FontManager;
import io.space.serverlist.ServerListManager;
import io.space.translate.TranslateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;

public final class Wrapper {
    public static Wrapper Instance;

    private final File CLIENT_DIRECTORY = new File(Minecraft.getMinecraft().mcDataDir,"HmXixClient/");
    private final Logger logger = LogManager.getLogger("HmXix");
    @SuppressWarnings("FieldCanBeLocal")
    private final String STRING_RANDOM_SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    @SuppressWarnings("FieldCanBeLocal")
    private final String CLIENT_VERSION = "121922";
    private NormalConfig NORMAL_CONFIG;
    private ScaledResolution scaledResolution;
    private ServerData lastConnectedServer;

    private float updateYaw;
    private float updatePitch;

    private int mouseX;
    private int mouseY;

    @Native
    public void init() {
        logger.info("Initializing client");
        logger.info("HmXixClient version {}",CLIENT_VERSION);

        Display.setTitle("HmXixClient " + CLIENT_VERSION);

        if (!CLIENT_DIRECTORY.exists()) {
            CLIENT_DIRECTORY.mkdir();
        }

        CommandManager.Instance = new CommandManager();
        CommandManager.Instance.init();

        ModManager.Instance = new ModManager();

        TranslateManager.Instance = new TranslateManager();

        try {
            TranslateManager.Instance.readTranslatedFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DesignerManager.Instance = new DesignerManager();

        GlobalSetting.Instance = new GlobalSetting();

        CapeManager.Instance = new CapeManager();

        NORMAL_CONFIG = new NormalConfig();

        AltManager.Instance = new AltManager();

        try {
            AltManager.Instance.readAlt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigManager.Instance = new ConfigManager();

        try {
            ConfigManager.Instance.loadConfig(NORMAL_CONFIG);
        } catch (Throwable e) {
            e.addSuppressed(new Throwable("Failed to load config"));

            e.printStackTrace();
        }

        ServerListManager.Instance = new ServerListManager();
        ServerListManager.Instance.init();

        ChatCodeManager.Instance = new ChatCodeManager();

        FontManager.init();

        NotificationManager.Instance = new NotificationManager();

        EventResponder.Instance = new EventResponder();

        final Thread thread = new Thread("Save Config Thread") {
            @Override
            public void run() {
                while (Minecraft.getMinecraft().running) {
                    try {
                        ConfigManager.Instance.saveConfig(NORMAL_CONFIG);
                        AltManager.Instance.saveAlt();

                        //noinspection BusyWait
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                interrupt();
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    public void updateRender() {
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        mouseX = Mouse.getX() * scaledResolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        mouseY = scaledResolution.getScaledHeight() - Mouse.getY() * scaledResolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }

    public void runTick() {

    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public static void sendMessage(Object o) {
        if (o == null) o = "null";

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[HmXixClient] " + EnumChatFormatting.GRAY + o));
    }

    public static void sendMessageOriginal(Object o) {
        if (o == null) o = "null";

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(o.toString()));
    }

    public static boolean isHovered(double x, double y, double x2, double y2, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public static double getEntitySpeed(Entity entity) {
        double xDif = entity.posX - entity.prevPosX;
        double zDif = entity.posZ - entity.prevPosZ;
        return (Math.sqrt(xDif * xDif + zDif * zDif) * 20.0);
    }

    public static void resetTimerSpeed() {
        Minecraft.getMinecraft().timer.timerSpeed = 1.0f;
    }

    public File getClientDirectory() {
        return CLIENT_DIRECTORY;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getStringRandomSeed() {
        return STRING_RANDOM_SEED;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public ServerData getLastConnectedServer() {
        return lastConnectedServer;
    }

    public void setLastConnectedServer(ServerData lastConnectedServer) {
        this.lastConnectedServer = lastConnectedServer;
    }

    public float getUpdateYaw() {
        return updateYaw;
    }

    public void setUpdateYaw(float updateYaw) {
        this.updateYaw = updateYaw;
    }

    public float getUpdatePitch() {
        return updatePitch;
    }

    public void setUpdatePitch(float updatePitch) {
        this.updatePitch = updatePitch;
    }

    public String getClientVersion() {
        return CLIENT_VERSION;
    }
}
