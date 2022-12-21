package io.space.mod;

import by.radioegor146.annotation.Native;
import io.space.Wrapper;
import io.space.mod.fight.*;
import io.space.mod.item.*;
import io.space.mod.move.*;
import io.space.mod.other.*;
import io.space.mod.player.*;
import io.space.mod.visual.*;
import io.space.mod.world.*;
import io.space.renderer.gui.clickgui.ClickGui;
import io.space.renderer.gui.dropdown.windows.components.implement.values.*;
import io.space.renderer.gui.dropdown.windows.窗口;
import io.space.value.Value;
import io.space.value.values.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class ModManager {
    public static ModManager Instance;

    private final LinkedHashMap<String,Mod> modMap = new LinkedHashMap<>();

    public ModManager() {
        init();
    }

    @Native
    private void init() {
        Wrapper.Instance.getLogger().info("Initializing mod manager");

        //Fight
        registerMods(new KillAura(),new AntiBot(),new Velocity(),new Clicker(),new HitBox(),new Reach(),new Critical(),new AutoAim(),new TPAura(),new AutoPot(),new FastThrow(),
                new Trigger(),new LegitAura(),new AutoSoup());

        //Visual
        registerMods(new HUD(),new NightVision(),new NightVision(),new NameTags(),new CamaraNoClip(),new WideAngle(),new ChestESP(),new TargetHUD(),new BlockHitting(),new BetterGui(),
                new ESP(),new Radar(),new ItemESP(),new DamageParticle(),new PacketGraph(),new FireRenderer(),new AnimationSneak(),new Arrow(),new MoreParticle(),new EntityHurtColor(),
                new AntiOpaqueBlock(),new NoPumpkinHead(),new BlockOverlay(),new EnchantColor(),new NoHurtCam(),new ItemPhysics());

        //Move
        registerMods(new Sprint(),new ScreenMove(),new NoJumpDelay(),new NoSlow(),new Speed(),new TargetStrafe(),new Fly(),new LongJump(),new Clip(),new FastLadder(),new SafeWalk(),new Teleport(),
                new LookTP(),new IceSpeed(),new AntiVoid(),new HighJump(),new AirJump(),new Blink(),new SlimeJump(),new Spider(),new Step(),new Jesus(),new KeepSprint(),new NoSneakSlow(),
                new Eagle(),new NoWeb(),new BlockSpeed(), new LegitSpeed());

        //Player
        registerMods(new NoFall(),new AntiEntityHinder(),new FastEat(),new FreeCam(),new FastPlace(),new NoRotation(),new FreeCamHit(),new AutoRespawn(),new AntiCactus(),new TPPick(),
                new FastHeal(), new SpeedMine());

        //Other
        registerMods(new Log4JPatcher(),new Teams(),new Disabler(),new PacketMonitor(),new PingSpoof(),new AutoFish(),new AutoDoor(),new Debug(),new PenShen(),new LagBackChecker(),
                new ChestAura());

        //Item
        registerMods(new ChestStealer(),new AutoArmor(),new InventoryManager(),new FastDrop(),new AutoTool());

        //World
        registerMods(new Scaffold(),new XRay(),new Timer(),new Breaker(),new Weather(),new Nuker(),new AntiInvisibility(),new LightningDetector(), new UHCFind());

        Wrapper.Instance.getLogger().info("总共加载了" + modMap.size() + "个Mod");
    }

    ClickGui gui = null;

    public void onKey(int keyCode) {
        if (keyCode == Keyboard.KEY_NONE) return;

        if (keyCode == Keyboard.KEY_RSHIFT) {
            if(gui == null)
                gui = new ClickGui();
            Minecraft.getMinecraft().displayGuiScreen(gui);
            return;
        }

        modMap.values().stream().filter(mod -> mod.getKeyCode() == keyCode).forEach(Mod::toggle);
    }

    public boolean getModEnable(String modName) {
        return getModFromName(modName).isEnable();
    }

    public Mod getModFromName(String modName) {
        return modMap.get(modName.toLowerCase());
    }

    public void registerMods(Mod... mods) {
        for (Mod mod : mods) {
            mod.window = new 窗口(mod.getModName());
            if (mod.getValues() != null) {
                for (Value s : mod.getValues()) {
                    if (s instanceof ModeValue) {
                        模式按钮 模式按钮 = new 模式按钮((ModeValue) s, mod.window, mod, mod.window.高度);
                        mod.window.添加组件(模式按钮);
                    }
                }
                for (Value s : mod.getValues()) {
                    if (s instanceof NumberValue) {
                        滑条按钮 滑条按钮 = new 滑条按钮((NumberValue) s, mod.window, mod.window.高度);
                        mod.window.添加组件(滑条按钮);
                    }
                }
                for (Value s : mod.getValues()) {
                    if (s instanceof ColorValue) {
                        取色器按钮 button = new 取色器按钮((ColorValue) s, mod.window, mod.window.高度);
                        mod.window.添加组件(button);
                    }
                }
                for (Value s : mod.getValues()) {
                    if (s instanceof TextValue) {
                        输入框按钮 button = new 输入框按钮((TextValue) s, mod.window, mod.window.高度);
                        mod.window.添加组件(button);
                    }
                }
                for (Value s : mod.getValues()) {
                    if (s instanceof BooleanValue) {
                        选项按钮 button = new 选项按钮((BooleanValue) s, mod.window, mod.window.高度);
                        mod.window.添加组件(button);
                    }
                }
            }
            modMap.put(mod.getModName().toLowerCase(),mod);
        }
    }

    public List<Mod> getCategoryMods(Mod.Category category) {
        return getModMap().values().stream().filter((mod -> mod.getCategory() == category)).collect(Collectors.toList());
    }

    public LinkedHashMap<String, Mod> getModMap() {
        return modMap;
    }
}
