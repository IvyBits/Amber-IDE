package amber.os.filechooser;

import amber.os.OS;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

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

        if (!OS.osLibrariesLoaded()) {
            throw new UnsupportedOperationException("AmberOS not loaded");
        }
    }

    public SwingFileDialog(String title) {
        this(title, null);
    }

    public boolean show() {
        browser = new JFileChooser(title);
        browser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        browser.setMultiSelectionEnabled(multi);
        browser.setCurrentDirectory(dir);
        return browser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION;
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
