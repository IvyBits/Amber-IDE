/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amber;

import amber.data.state.LazyState;
import amber.data.state.Scope;
import amber.data.state.node.IState;
import amber.swing.UIUtil;

import javax.swing.*;
import java.awt.Font;

/**
 *
 * @author xiaomao
 */
public class Settings {
    public static final int SETTINGS = 2;

    static {
        String _font = amber.os.Utilities.getFont();
        if (_font == null)
            _font = UIManager.getFont("Label.font").getFontName();
        defaultFont = _font;
        System.out.println("Font chosen: " + _font);

        Scope.defineScope(SETTINGS, "${PROJECT.DIR}", "settings");
        IState state = Amber.getStateManager().getState(SETTINGS, "uiFont");
        if (state != null) {
           font = (String) state.get();
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
