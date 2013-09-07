package tk.amberide.engine.input.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Stack;
import javax.swing.SwingUtilities;

/**
 *
 * @author Tudor
 */
public class AWTMouse {

    private static final Stack<MouseEvent> events = new Stack<MouseEvent>();
    private static MouseEvent currentEvent;
    private static HashSet<Integer> buttonDownBuffer = new HashSet<Integer>();
    private static Point lastPoint = new Point(0, 0), grabEnter;
    private static WeakReference<Component> grabbedParent;
    private static AWTEventListener dispatch;
    private static int scroll;

    public static void create() {
        Toolkit.getDefaultToolkit().addAWTEventListener(dispatch = new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                if (e instanceof MouseWheelEvent) {
                    scroll -= ((MouseWheelEvent) e).getWheelRotation(); // TODO: check, this doesn't seem to work on all comps.
                }
                if (e != null && e.getSource() instanceof Component && ((Component) e.getSource()).equals(focused())) {
                    if (e instanceof MouseEvent) {
                        MouseEvent event = (MouseEvent) e;
                        events.push(event);
                        switch (event.getID()) {
                            case MouseEvent.MOUSE_PRESSED:
                                buttonDownBuffer.add(AWTInputMap.map(event));
                                break;
                            case MouseEvent.MOUSE_RELEASED:
                                buttonDownBuffer.remove(AWTInputMap.map(event));
                                break;
                        }
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    private static Component focused() {
        for (final Window w : Window.getWindows()) {
            if (w.isFocused()) {
                return w.getFocusOwner();
            }
        }
        return null;
    }

    public static void destroy() {
        if (isCreated()) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(dispatch);
            dispatch = null;
            events.clear();
            buttonDownBuffer.clear();
            grabbedParent = null;
        }
    }

    protected static void ensureCreated() {
        if (!isCreated()) {
            throw new IllegalStateException("AWTMouse is not created");
        }
    }

    public static boolean isCreated() {
        return dispatch != null;
    }

    public static boolean isButtonDown(int button) {
        ensureCreated();
        return buttonDownBuffer.contains(button);
    }

    public static boolean next() {
        if (!events.empty()) {
            currentEvent = events.pop();
            return true;
        }
        currentEvent = null;
        return false;
    }

    public static int getEventX() {
        ensureCreated();
        if (currentEvent != null) {
            return currentEvent.getX();
        }
        return -1;
    }

    public static int getEventY() {
        ensureCreated();
        if (currentEvent != null) {
            return currentEvent.getY();
        }
        return -1;
    }

    public static int getDWheel() {
        ensureCreated();
        int delta = scroll;
        scroll = 0;
        return delta;
    }

    public static int getEventButton() {
        ensureCreated();
        if (currentEvent != null) {
            return AWTInputMap.map(currentEvent);
        }
        return 0;
    }

    public static boolean getEventButtonState() {
        ensureCreated();
        if (currentEvent != null) {
            switch (currentEvent.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    return true;
                case MouseEvent.MOUSE_RELEASED:
                    return false;
            }
        }
        return false;
    }

    public static int getX() {
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    public static int getY() {
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    public static int getDX() {
        return MouseInfo.getPointerInfo().getLocation().x - lastPoint.x;
    }

    public static int getDY() {
        return -(MouseInfo.getPointerInfo().getLocation().y - lastPoint.y);
    }

    public static int getY(Component relative) {
        return relative.getHeight() - convert(MouseInfo.getPointerInfo().getLocation(), relative).y; // To OGL coords
    }

    public static int getX(Component relative) {
        return convert(MouseInfo.getPointerInfo().getLocation(), relative).x;
    }

    protected static Point convert(Point point, Component c) {
        SwingUtilities.convertPointFromScreen(point, c);
        return point;
    }

    public static void poll() {
        if (isGrabbed()) {
            Component parent = grabbedParent.get();
            int threshold = Math.min((int) (parent.getWidth() - 5), (int) (parent.getHeight() - 5));
            Point collision = new Point(lastPoint);
            SwingUtilities.convertPointFromScreen(collision, parent);
            if (collision.y <= threshold
                    || collision.x <= threshold
                    || collision.y >= parent.getHeight() - threshold
                    || collision.x >= parent.getWidth() - threshold) {
                Point center = new Point(parent.getWidth() / 2, parent.getHeight() / 2);
                SwingUtilities.convertPointToScreen(center, parent);
                try {
                    new Robot().mouseMove(center.x, center.y);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        }
        lastPoint = MouseInfo.getPointerInfo().getLocation();
    }

    public static int getButtonCount() {
        return MouseInfo.getNumberOfButtons();
    }

    public static void setGrabbed(boolean grab) {
        for (final Window w : Window.getWindows()) {
            if (w.isFocused()) {
                if (grab) {
                    Component parent = w.findComponentAt(convert(MouseInfo.getPointerInfo().getLocation(), w));
                    if (parent != null) {
                        if (grabbedParent != null && grabbedParent.get() == parent) {
                            return;
                        }
                        grabEnter = convert(MouseInfo.getPointerInfo().getLocation(), parent);
                        parent.setCursor(Toolkit.getDefaultToolkit().
                                createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor"));
                        grabbedParent = new WeakReference<Component>(parent);
                        w.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                setGrabbed(false);
                                w.removeFocusListener(this);
                            }
                        });
                    }
                } else if (isGrabbed()) {
                    Component parent = grabbedParent.get();
                    parent.setCursor(Cursor.getDefaultCursor());
                    try {
                        SwingUtilities.convertPointToScreen(grabEnter, parent);
                        new Robot().mouseMove(grabEnter.x, grabEnter.y);
                    } catch (AWTException ex) {
                        ex.printStackTrace();
                    }
                    grabbedParent = null;
                    grabEnter = null;
                }
            }
        }
    }

    public static boolean isGrabbed() {
        return grabbedParent != null && grabbedParent.get() != null;
    }
}
