package tk.amberide.engine.input.awt;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import java.util.HashSet;
import java.util.Stack;

public final class AWTKeyboard {

    private static final Stack<KeyEvent> events = new Stack<KeyEvent>();
    private static KeyEvent currentEvent;
    private static HashSet<Integer> keyDownBuffer = new HashSet<Integer>();
    private static AWTEventListener dispatch;

    public static void create() {
        Toolkit.getDefaultToolkit().addAWTEventListener(dispatch = new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                if (e instanceof KeyEvent) {
                    KeyEvent event = (KeyEvent) e;
                    events.push(event);
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keyDownBuffer.add(AWTInputMap.map(event));
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keyDownBuffer.remove(AWTInputMap.map(event));
                            break;
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }

    public static void destroy() {
        if (isCreated()) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(dispatch);
            events.clear();
            keyDownBuffer.clear();
            dispatch = null;
            currentEvent = null;
        }
    }

    protected static void ensureCreated() {
        if (!isCreated()) {
            throw new IllegalStateException("AWTKeyboard is not created");
        }
    }

    public static boolean isCreated() {
        return dispatch != null;
    }

    public static int getNumKeyboardEvents() {
        ensureCreated();
        return events.size();
    }

    public static boolean isKeyDown(int key) {
        ensureCreated();
        return keyDownBuffer.contains(key);
    }

    public static boolean next() {
        ensureCreated();
        if (!events.empty()) {
            currentEvent = events.pop();
            return true;
        }
        currentEvent = null;
        return false;
    }

    public static char getEventCharacter() {
        ensureCreated();
        if (currentEvent != null) {
            return currentEvent.getKeyChar();
        }
        return '\0';
    }

    public static int getEventKey() {
        ensureCreated();
        if (currentEvent != null) {
            return AWTInputMap.map(currentEvent);
        }
        return -1;
    }

    public static boolean getEventKeyState() {
        ensureCreated();
        if (currentEvent != null) {
            return currentEvent.getID() == KeyEvent.KEY_PRESSED;
        }
        return false;
    }
}
