package amber.os.filechooser;

import amber.os.OS;
import amber.os.Win;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class WinFileDialog implements IFileDialog {

    private Component parent = null;
    private String filter = "All Files\0*.*\0\0";
    private String file = null;
    private String[] files = null;
    private String initial = null;
    private String title = null;
    private long hwnd = 0;
    private long error = 0;
    private boolean multi = false;

    public WinFileDialog(String title, Component parent) {
        this.title = title;
        setParent(parent);
        if (!OS.osLibrariesLoaded()) {
            throw new UnsupportedOperationException("AmberOS not loaded");
        }
    }

    public WinFileDialog(String title) {
        this(title, null);
    }

    public boolean show() {
        return showNative();
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
        hwnd = Win.getHWND(parent);
    }

    public String getFilter() {
        return filter.substring(0, filter.length() - 2).replace('\0', '|');
    }

    public void setFilter(String filter) {
        this.filter = filter.replace('|', '\0') + "\0\0";
    }

    public File getInitial() {
        return initial != null ? new File(initial) : null;
    }

    public void setInitial(File initial) {
        this.initial = initial.getAbsolutePath();
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
        return new File(file);
    }

    public File[] getFiles() {
        File[] fl = new File[files.length];
        for (int i = 0; i != fl.length; i++) {
            fl[i] = new File(files[i]);
        }
        return fl;
    }

    public native boolean showNative();
}
