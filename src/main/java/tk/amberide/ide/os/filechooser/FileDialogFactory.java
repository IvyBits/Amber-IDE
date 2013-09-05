package tk.amberide.ide.os.filechooser;

import tk.amberide.ide.os.OS;
import java.awt.Component;
import javax.swing.UIManager;

/**
 *
 * @author Tudor
 */
public class FileDialogFactory {

    public static IFileDialog newFileDialog(String title, Component parent) {
        if (OS.osLibrariesLoaded() && UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName())) {
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
