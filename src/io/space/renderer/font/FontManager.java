package io.space.renderer.font;

import java.awt.*;

public final class FontManager {
    public static final FontDrawer default16;
    public static final FontDrawer default18;
    public static final FontDrawer default22;

    static {
        default16 = getDefault(16);
        default18 = getDefault(18);
        default22 = getDefault(22);
    }

    public static void init() {

    }

    private static FontDrawer getDefault(int size) {
        try {
            return new FontDrawer(Font.createFont(Font.TRUETYPE_FONT, FontManager.class.getResourceAsStream("/assets/space/font.ttf")).deriveFont(Font.PLAIN, size), true, true, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new FontDrawer(new Font(Font.SANS_SERIF, Font.PLAIN, 16), false, true, false);
    }
}
