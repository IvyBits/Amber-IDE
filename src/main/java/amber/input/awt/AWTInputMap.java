package amber.input.awt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;

final class AWTInputMap {

    private static final HashMap<Integer, Integer> keyMap = new HashMap<Integer, Integer>();

    static {
        for (Field key : KeyEvent.class.getDeclaredFields()) {
            String name = key.getName();
            if (name.startsWith("VK_")) {
                try {
                    int keyCode = key.getInt(null);
                    keyMap.put(keyCode, map(keyCode, name));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static int map(int keyCode, String name) {
        switch (keyCode) {
            case KeyEvent.VK_BACK_SPACE:
                return Keyboard.KEY_BACKSLASH;
            case KeyEvent.VK_OPEN_BRACKET:
                return Keyboard.KEY_LBRACKET;
            case KeyEvent.VK_CLOSE_BRACKET:
                return Keyboard.KEY_RBRACKET;
            case KeyEvent.VK_QUOTE:
                return Keyboard.KEY_APOSTROPHE;
            case KeyEvent.VK_NUMBER_SIGN:
                return Keyboard.KEY_GRAVE;
            case KeyEvent.VK_CAPS_LOCK:
                return Keyboard.KEY_CAPITAL;
            case KeyEvent.VK_NUM_LOCK:
                return Keyboard.KEY_NUMLOCK;
            case KeyEvent.VK_SCROLL_LOCK:
                return Keyboard.KEY_SCROLL;
            case KeyEvent.VK_NONCONVERT:
                return Keyboard.KEY_NOCONVERT;
            case KeyEvent.VK_UNDERSCORE:
                return Keyboard.KEY_UNDERLINE;
            case KeyEvent.VK_BACK_SLASH:
                return Keyboard.KEY_BACKSLASH;
            case KeyEvent.VK_UNDEFINED:
                return -1;
            default:
                try {
                    return Keyboard.class.getDeclaredField("KEY" + name.substring(name.indexOf("_"), name.length())).getInt(null);
                } catch (Exception ex) {
                }
        }
        return -1;
    }

    public static int mapKeyCode(int keyCode) {
        return keyMap.get(keyCode);
    }

    public static int map(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int location = event.getKeyLocation();
        switch (keyCode) {
            case KeyEvent.VK_CONTROL:
                if (location == KeyEvent.KEY_LOCATION_RIGHT) {
                    return Keyboard.KEY_RCONTROL;
                } else {
                    return Keyboard.KEY_LCONTROL;
                }
            case KeyEvent.VK_SHIFT:
                if (location == KeyEvent.KEY_LOCATION_RIGHT) {
                    return Keyboard.KEY_RSHIFT;
                } else {
                    return Keyboard.KEY_LSHIFT;
                }
            case KeyEvent.VK_ALT:
                if (location == KeyEvent.KEY_LOCATION_RIGHT) {
                    return Keyboard.KEY_RMENU;
                } else {
                    return Keyboard.KEY_LMENU;
                }
            case KeyEvent.VK_ENTER:
                if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
                    return Keyboard.KEY_NUMPADENTER;
                } else {
                    return Keyboard.KEY_RETURN;
                }
            case KeyEvent.VK_COMMA:
                if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
                    return Keyboard.KEY_NUMPADCOMMA;
                } else {
                    return Keyboard.KEY_COMMA;
                }
            default:
                return mapKeyCode(keyCode);
        }
    }

    public static int map(MouseEvent event) {
        return mapButton(event.getButton());
    }

    public static int mapButton(int button) {
        switch (button) {
            case MouseEvent.BUTTON1:
                return 0;
            case MouseEvent.BUTTON2:
                return 2;
            case MouseEvent.BUTTON3:
                return 1;
            default:
                return -1;
        }
    }
}