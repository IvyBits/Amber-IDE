package amber;

import amber.data.state.LazyState;
import amber.data.state.Scope;
import amber.data.state.node.IState;
import amber.os.OS;
import amber.swing.UIUtil;

import java.awt.Font;
import java.awt.Window;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author xiaomao
 */
public class Settings {

    public static final int SETTINGS = 2;

    static {
        Scope.defineScope(SETTINGS, "${PROJECT.DIR}", "settings");
    }

    public static void load() throws Exception {
        String _font = OS.getSystemFontName();
        defaultFont = _font;
        Amber.getStateManager().clearStates(SETTINGS);
        Amber.getStateManager().loadStates(SETTINGS);
        IState fontState = Amber.getStateManager().getState(SETTINGS, "uiFont");
        if (fontState != null) {
            font = (String) fontState.get();
        }
        IState sizeState = Amber.getStateManager().getState(SETTINGS, "uiFontSize");
        if (sizeState != null) {
            size = (Integer) sizeState.get();
        }
        updateFont();

        IState lafState = Amber.getStateManager().getState(SETTINGS, "uiLaF");
        if (lafState != null) {
            laf = (String) lafState.get();
        }
        updateLaF();
        Amber.getStateManager().registerStateOwner(Settings.class);
    }
    protected static String defaultFont;
    protected static String font = null;
    protected static int size = 12;
    protected static String laf;

    public static Font getUIFont() {
        return new Font(font == null ? defaultFont : font, Font.PLAIN, size);
    }

    public static void setUIFont(Font fn) {
        font = fn.getName();
        size = fn.getSize();
    }

    public static void updateFont() {
        UIUtil.setUIFont(getUIFont());
    }

    public static String getLaFClassName() {
        return laf;
    }

    public static void setLaFClassName(String name) {
        laf = name;
    }

    public static void updateLaF() {
        try {
            if (laf != null) {
                UIManager.setLookAndFeel(laf);
            } else {
                UIUtil.makeNative();
                laf = UIManager.getSystemLookAndFeelClassName();
            }
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @LazyState(scope = SETTINGS, name = "uiFont")
    private static String uiFont() {
        return font;
    }

    @LazyState(scope = SETTINGS, name = "uiFontSize")
    private static int uiFontSize() {
        return size;
    }

    @LazyState(scope = SETTINGS, name = "uiLaF")
    private static String uiLaF() {
        return laf;
    }
}
