package amber.os.filechooser;

import amber.data.io.FileIO;
import amber.os.OS;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Tudor
 */
public class SwingFileDialog implements IFileDialog {

    private JFileChooser browser;
    private Component parent;
    private String filter;
    private File dir;
    private String title;
    private boolean multi;

    public SwingFileDialog(String title, Component parent) {
        browser = new JFileChooser(title);
        this.parent = parent;
        if (!OS.osLibrariesLoaded()) {
            throw new UnsupportedOperationException("AmberOS not loaded");
        }
    }

    public SwingFileDialog(String title) {
        this(title, null);
    }

    public boolean show() {
        browser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        browser.setMultiSelectionEnabled(multi);
        browser.setCurrentDirectory(dir);
        if (filter != null) {
            final String[] split = filter.split("\\|");
            for (int i = 0; i != split.length; i += 2) {
                final String description = split[i];
                final List<String> exts = Arrays.asList(split[i + 1].split(";"));
                browser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || exts.contains("*." + FileIO.getFileExtension(f));
                    }

                    @Override
                    public String getDescription() {
                        return description;
                    }
                });
            }
        }
        int ret = browser.showOpenDialog(parent);
       // parent.setVisible(true);
        return ret == JFileChooser.APPROVE_OPTION;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setInitial(File initial) {
        browser.setCurrentDirectory(initial);
    }

    public File getInitial() {
        return browser.getCurrentDirectory();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public File getFile() {
        return browser.getSelectedFile();
    }

    public File[] getFiles() {
        return browser != null ? browser.getSelectedFiles() : new File[0];
    }
}
