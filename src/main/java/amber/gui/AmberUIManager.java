package amber.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

/**
 *
 * @author Tudor
 */
public class AmberUIManager {

    public static void setup() {
        UIManager.put("MapEditor.font", new Font("Courier", Font.PLAIN, 15));
        UIManager.put("MapEditor.background", Color.WHITE);
    }
}
