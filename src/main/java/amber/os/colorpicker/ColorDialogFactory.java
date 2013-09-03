package amber.os.colorpicker;

import amber.os.OS;
import static amber.os.OS.Platform.WINDOWS;
import java.awt.Component;
import javax.swing.UIManager;

/**
 *
 * @author Tudor
 */
public class ColorDialogFactory {

    public static IColorDialog newFileDialog(Component parent) {
        if (OS.osLibrariesLoaded() && UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName())) {
            switch (OS.getPlatform()) {
                case WINDOWS:
                    return new WinColorDialog(parent);
            }
        }
        return new SwingColorDialog(parent);
    }
}
