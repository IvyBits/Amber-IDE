package amber;

import amber.data.state.LazyState;
import amber.data.state.Scope;
import amber.data.state.node.IState;
import amber.os.OS;
import amber.swing.UIUtil;

import java.awt.Font;

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
        IState state = Amber.getStateManager().getState(SETTINGS, "uiFont");
        if (state != null) {
            font = (String) state.get();
            setFont();
        }
        Amber.getStateManager().registerStateOwner(Settings.class);
    }
    protected static String defaultFont;
    protected static String font = null;

    @LazyState(scope = SETTINGS, name = "uiFont")
    public static String uiFont() {
        return font;
    }

    public static Font getUIFont() {
        return new Font(font == null ? defaultFont : font, Font.PLAIN, 12);
    }

    public static void setFont() {
        UIUtil.setUIFont(getUIFont());
    }
}
