package amber.os.filechooser;

import amber.os.OS;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public class FileDialogFactory {

    public static IFileDialog newFileDialog(String title, Component parent) {
        if (OS.osLibrariesLoaded()) {
            switch (OS.getPlatform()) {
                case WINDOWS:
                    return new WinFileDialog(title, parent);
            }
        }
        return new SwingFileDialog(title, parent);
    }

    public static IFileDialog newFileDialog(String title) {
        return newFileDialog(title, null);
    }
}
