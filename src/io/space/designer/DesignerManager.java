package io.space.designer;

import io.space.designer.designerimpl.PacketGraphDesigner;
import io.space.designer.designerimpl.RadarDesigner;
import io.space.designer.designerimpl.TargetHUDDesigner;

import java.util.ArrayList;
import java.util.Arrays;

public final class DesignerManager {
    public static DesignerManager Instance;

    private final ArrayList<Designer> designers = new ArrayList<>();

    public DesignerManager() {
        registerDesigners(new RadarDesigner(),new PacketGraphDesigner(),new TargetHUDDesigner());
    }

    private void registerDesigners(Designer... designers) {
        this.designers.addAll(Arrays.asList(designers));
    }

    public ArrayList<Designer> getDesigners() {
        return designers;
    }
}
