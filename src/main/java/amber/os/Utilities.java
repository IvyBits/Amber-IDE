package amber.os;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

public class Utilities {
    private static native String nativeGetFont();
    private static String nativeFont = null;
    private static boolean noNative = false;
    public static String getFont() {
        if (noNative)
            return null;
        if (nativeFont == null)
            nativeFont = nativeGetFont();
        if (nativeFont == null)
            noNative = true;
        return nativeFont;
    }

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

    public static long getHWND(Component component) {
        Class<?> WComponentPeer;
        try {
            WComponentPeer = Class.forName("sun.awt.windows.WComponentPeer");
        } catch (ClassNotFoundException e) {
            return 0;
        }

        if (component.getPeer() == null)
            return 0;

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

    public static void main(String[] args) {
        System.out.println("System default font: " + getFont());
    }
}
