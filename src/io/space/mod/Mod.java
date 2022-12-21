package io.space.mod;

import com.darkmagician6.eventapi.EventManager;
import io.space.command.CommandManager;
import io.space.events.EventToggleMod;
import io.space.global.GlobalSetting;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.renderer.gui.dropdown.windows.窗口;
import io.space.renderer.gui.dropdown.点击界面;
import io.space.utils.SoundFxPlayer;
import io.space.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Mod {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    public 窗口 window;
    private final ArrayList<Value<?>> values = new ArrayList<>();
    private final String modName;
    private final Category category;

    private String translatedName;
    private int keyCode = Keyboard.KEY_NONE;
    private boolean enable;

    public double animationX,animationY;

    public Mod(String modName, Category category) {
        this.modName = modName;
        this.category = category;

        CommandManager.Instance.registerCommands(new ModValueSettingCommand(this));
    }

    public final String getModRenderNameWithTag() {
        final String modTag = getModTag();

        if (modTag == null) {
            return getRenderName();
        }

        return getRenderName() + " " + EnumChatFormatting.GRAY + modTag;
    }

    public final String getModRenderNameWithTagNoColor() {
        final String modTag = getModTag();

        if (modTag == null) {
            return getRenderName();
        }

        return getRenderName() + " " + modTag;
    }

    protected String getModTag() {
        return null;
    }

    public final boolean isEnable() {
        return enable;
    }

    public final void toggle() {
        setEnable(!enable);
    }

    public final void setEnable(boolean enable) {
        final EventToggleMod eventToggleMod = new EventToggleMod(this, this.enable, enable);
        EventManager.call(eventToggleMod);

        if (eventToggleMod.isCancelled()) return;
        if (this.enable == enable) return;

        this.enable = enable;

        if (enable) {
            if (mc.world != null) {
                onEnable();

                if(点击界面.声音.getValue()) {
                    SoundFxPlayer.playSound(SoundFxPlayer.ENABLE_MOD);
                }

                NotificationManager.Instance.addNotification("Toggle","Enabled " + modName, Notification.NotificationType.INFO,1000);
            }

            EventManager.register(this);
        } else {
            EventManager.unregister(this);

            if (mc.world != null) {
                if(点击界面.声音.getValue()) {
                    SoundFxPlayer.playSound(SoundFxPlayer.DISABLE_MOD);
                }

                NotificationManager.Instance.addNotification("Toggle","Disabled " + modName, Notification.NotificationType.WARNING,1000);

                onDisable();
            }
        }
    }

    protected final void registerValues(Value<?>... values) {
        this.values.addAll(Arrays.asList(values));
    }

    public final ArrayList<Value<?>> getValues() {
        return values;
    }

    public final int getKeyCode() {
        return keyCode;
    }

    public final void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public final String getModName() {
        return modName;
    }

    public final Category getCategory() {
        return category;
    }

    public final String getTranslatedName() {
        return translatedName;
    }

    public final void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public final String getRenderName() {
        return translatedName == null || !GlobalSetting.Instance.getTranslateModName().getValue() ? modName : translatedName;
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public boolean hide = false;

    public void setHide (boolean b) {
        hide = b;
    }

    public enum Category {
        FIGHT("Fight"),
        VISUAL("Visual"),
        MOVE("Move"),
        PLAYER("Player"),
        OTHER("Other"),
        ITEM("Item"),
        WORLD("World");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
