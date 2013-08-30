package amber.gui;

import amber.Settings;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.UIManager;

/**
 *
 * @author Tudor
 */
public class AmberUIManager {
    protected static Set<String> fonts = new HashSet<String>(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    public static String available(String... list) {
        for (String name : list)
            if (fonts.contains(name))
                return name;
        return Font.SANS_SERIF;
    }

    public static void setup() {
        UIManager.put("MapEditor.font", new Font(available("Consolas", "DejaVu Sans Mono", "Inconsolata", "Courier New", "Courier"), Font.PLAIN, 15));
        UIManager.put("MapEditor.background", Color.WHITE);
        Settings.updateFont();
    }
}
