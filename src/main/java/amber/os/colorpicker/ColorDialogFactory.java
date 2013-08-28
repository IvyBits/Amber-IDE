package amber.os.colorpicker;

import amber.os.OS;
import static amber.os.OS.Platform.WINDOWS;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public class ColorDialogFactory {

    public static IColorDialog newFileDialog(Component parent) {
        if (OS.osLibrariesLoaded()) {
            switch (OS.getPlatform()) {
                case WINDOWS:
                    return new WinColorDialog(parent);
            }
        }
        return new SwingColorDialog(parent);
    }
}
