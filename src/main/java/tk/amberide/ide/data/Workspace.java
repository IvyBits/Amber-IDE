package tk.amberide.ide.data;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tudor
 */
public class Workspace {

    private File dataDir;
    private File root;
    private Set<String> openedHandles = new HashSet<String>();

    public Workspace(File root) {
        this.root = root;

        dataDir = new File(root, ".amber");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public File getDataDirectory() {
        return dataDir;
    }

    public Set<String> getOpenedFiles() {
        return openedHandles;
    }

    public File getRootDirectory() {
        return root;
    }
}
