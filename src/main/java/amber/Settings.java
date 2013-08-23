/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amber;

import amber.data.state.LazyState;
import amber.data.state.Scope;
import amber.data.state.node.IState;
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
         IState state = Amber.getStateManager().getState(SETTINGS, "uiFont");
         if (state != null) {
            font = (String) state.get();
         }
         Amber.getStateManager().registerStateOwner(Settings.class);
    }

    protected static String font = null;
    
    @LazyState(scope = SETTINGS, name = "uiFont")
    public static String uiFont() {
         return font;
    }
    
    public static Font getUIFont() {
        return new Font(font == null ? Font.SANS_SERIF : font, Font.PLAIN, 12);
    }
    
    public static void setFont() {
        UIUtil.setUIFont(getUIFont());
    }
}
