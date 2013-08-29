package amber.gui.misc;

import amber.data.io.FileIO;
import java.io.File;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Tudor
 */
public class FileSystemIcon {
    
    private static final HashMap<String, Icon> icons = new HashMap<String, Icon>();

    public static void setIcon(String ext, Icon icon) {
        icons.put(ext, icon);
    }
    
    public static Icon getIcon(String ext) {
        return icons.get(ext);
    }
    
    public static Icon getIcon(File file) {
        String ext = FileIO.getFileExtension(file);
        if(icons.containsKey(ext)) {
            return icons.get(ext);
        }
        return FileSystemView.getFileSystemView().getSystemIcon(file);
    }
    
    public static void removeIcon(String ext) {
        icons.remove(ext);
    }
    
    public static boolean iconExists(String ext) {
        return icons.containsKey(ext);
    }
}
