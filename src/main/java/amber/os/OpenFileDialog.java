package amber.os;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenFileDialog {
    private Component parent = null;
    private String filter = "All Files\0*.*\0\0";
    private String file = null;
    private String[] files = null;
    private String initial = null;
    private String title = null;
    private long hwnd = 0;
    private long error = 0;
    private boolean multi = false;

    private static boolean noNative;
    static {
        try {
            System.loadLibrary("amberos32");
        } catch (SecurityException e) {
            noNative = true;
        } catch (UnsatisfiedLinkError e) {
            noNative = true;
        }

        if (noNative) {
            System.out.println("Can't load amberOS, OS-specific utilities will not work");
        }
    }

    public static boolean hasNative() {
        return !noNative;
    }

    public OpenFileDialog() {
        if (noNative)
            throw new UnsupportedOperationException("amberOS not loaded");
    }

    public native boolean showNative();

    public boolean show() {
        return showNative();
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
        hwnd = Utilities.getHWND(parent);
    }

    public String getFilter() {
        return filter.substring(0, filter.length() - 2).replace('\0', '|');
    }

    public void setFilter(String filter) {
        this.filter = filter.replace('|', '\0') + "\0\0";
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
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

    public List<String> getFiles() {
        return Arrays.asList(files);
    }

    public static void main(String[] args) throws IOException {
        OpenFileDialog ofd = new OpenFileDialog();
        System.out.println("Most Basic Test");
        if (ofd.show())
            System.out.println(ofd.getFile());

        System.out.println("In home directory");
        ofd.setTitle("Title and Initial...");
        ofd.setInitial(System.getProperty("user.home"));
        ofd.setFile(null);
        if (ofd.show())
            System.out.println(ofd.getFile());

        System.out.println("Title and Filter");
        ofd.setTitle("Title and Filter...");
        ofd.setFilter("C++ File|*.cpp;*.cxx;*.h;*.hpp|C File|*.c;*.h|All Files|*");
        ofd.setInitial(null);
        ofd.setFile(null);
        if (ofd.show())
            System.out.println(ofd.getFile());

        System.out.println("with default file");
        ofd.setTitle("Title and Filter with default...");
        if (ofd.show())
            System.out.println(ofd.getFile());

        System.out.println("with multi");
        ofd.setMulti(true);
        ofd.setTitle("Title and Filter with default and multi...");
        if (ofd.show()) {
            for (String file : ofd.getFiles())
                System.out.println(file);
        }
    }
}
