package amber.os;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import javax.swing.UIManager;

/**
 *
 * @author Tudor
 */
public class Win {
    private static native String nativeGetFont();

    public static String getSystemFontName() {
        return nativeGetFont();
    }
    public static long getHWND(Component component) {
        Class<?> WComponentPeer;
        try {
            WComponentPeer = Class.forName("sun.awt.windows.WComponentPeer");
        } catch (ClassNotFoundException e) {
            return 0;
        }

        if (component.getPeer() == null) {
            return 0;
        }

        Object c;
        try {
            c = WComponentPeer.cast(component.getPeer());
        } catch (ClassCastException e) {
            return 0;
        }

        try {
            return (Long) WComponentPeer.getMethod("getHWnd").invoke(c);
        } catch (IllegalAccessException e) {
            return 0;
        } catch (InvocationTargetException e) {
            return 0;
        } catch (NoSuchMethodException e) {
            return 0;
        }
    }
}
