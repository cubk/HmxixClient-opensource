package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import utils.hodgepodge.io.IOUtils;
import utils.hodgepodge.object.time.TimerUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class PenShen extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Local",new String[]{"Local","Custom"});
    private final NumberValue delay = new NumberValue("Delay",1000,0,10000,1);

    private final TimerUtils timerUtils = new TimerUtils(true);
    private final String[] lol = new String[1590];
    private String[] custom;

    public PenShen() {
        super("PenShen",Category.OTHER);
        registerValues(mode,delay);

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(IOUtils.getResourceAsStream("assets/space/penshen.txt")));
            for (int i = 0; i < lol.length; i++) {
                final String s = reader.readLine();

                lol[i] = s;
            }
        } catch (IOException e) {
            Wrapper.Instance.getLogger().error("呃... 初始化喷神(Local)的过程中发生了问题",e);
        } finally {
            if (reader != null) {
                IOUtils.closeQuietly(reader);
                reader = null;
            }
        }

        final File file = new File(Wrapper.Instance.getClientDirectory(),"penshen.txt");

        if (file.exists()) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                final ArrayList<String> list = new ArrayList<>();
                int lines = 0;
                String s;

                while ((s = reader.readLine()) != null) {
                    if (!s.isEmpty()) {
                        list.add(s);
                    }

                    lines++;
                }

                custom = new String[lines];
                for (int i = 0; i < list.size(); i++) {
                    custom[i] = list.get(i);
                }
            } catch (IOException e) {
                Wrapper.Instance.getLogger().error("呃... 初始化喷神(Custom)的过程中发生了问题", e);
            } finally {
                if (reader != null) {
                    IOUtils.closeQuietly(reader);
                }
            }
        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (timerUtils.hasReached(delay.getValue())) {
            if (mode.isCurrentMode("Local")) {
                mc.player.sendChatMessage(lol[ThreadLocalRandom.current().nextInt(0, lol.length - 1)]);
            } else if (mode.isCurrentMode("Custom")) {
                if (custom != null && custom.length > 0) {
                    mc.player.sendChatMessage(custom[ThreadLocalRandom.current().nextInt(0, custom.length - 1)]);
                }
            }
        }
    }
}
