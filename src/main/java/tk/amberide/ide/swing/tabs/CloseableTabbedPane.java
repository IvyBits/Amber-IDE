package tk.amberide.ide.swing.tabs;

import tk.amberide.ide.swing.MenuBuilder;
import tk.amberide.ide.swing.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * @author Tudor
 */
public class CloseableTabbedPane extends JTabbedPane {

    private HashMap<Integer, String> titles = new HashMap<Integer, String>();
    private List<TabCloseListener> closers = new ArrayList<TabCloseListener>();

    public CloseableTabbedPane() {
        super();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final int index = getUI().tabForCoordinate(CloseableTabbedPane.this, e.getX(), e.getY());
                if (index == -1)
                    return;
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    if (getTabComponentAt(index) instanceof CloseableTabComponent)
                        ((CloseableTabComponent) getTabComponentAt(index)).getCloseButton().doClick();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    e.consume();
                    new MenuBuilder("").addButton("Switch", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            if (getSelectedIndex() != index)
                                setSelectedIndex(index);
                            getSelectedComponent().requestFocusInWindow();
                        }
                    }).addSeparator().addButton("Close", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            if (getTabComponentAt(index) instanceof CloseableTabComponent)
                                ((CloseableTabComponent) getTabComponentAt(index)).getCloseButton().doClick();
                        }
                    }).create().getPopupMenu().show(CloseableTabbedPane.this, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    e.consume();
            }
        });

        UIUtil.mapInput(this, JComponent.WHEN_IN_FOCUSED_WINDOW, KeyEvent.VK_W, InputEvent.CTRL_MASK, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Component component = getTabComponentAt(getSelectedIndex());
                if (component instanceof CloseableTabComponent)
                    ((CloseableTabComponent) component).getCloseButton().doClick();
            }
        });
    }

    public void addTabCloseListener(TabCloseListener listener) {
        closers.add(listener);
    }

    public void removeTabCloseListener(TabCloseListener listener) {
        closers.remove(listener);
    }

    @Override
    public String getTitleAt(int index) {
        return titles.get(index);
    }

    @Override
    public void setTitleAt(int index, String name) {
        titles.put(index, name);
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        System.out.println("Removing " + comp);
        int index = this.indexOfComponent(comp);
        if (index != -1) {
            this.removeTabAt(index);
        }
    }

    public static class CloseableTabComponent extends JPanel {

        private JButton closeButton;
        private JLabel titleLabel;

        public CloseableTabComponent(String title) {
            super(new GridBagLayout());
            closeButton = new JButton("") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    if (getModel().isPressed()) {
                        g2.translate(1, 1);
                    }
                    g2.setStroke(new Stroke() {
                        BasicStroke stroke1 = new BasicStroke(1),
                                stroke2 = new BasicStroke(1);

                        public Shape createStrokedShape(Shape s) {
                            return stroke2.createStrokedShape(stroke1.createStrokedShape(s));
                        }
                    });

                    g2.setColor(getModel().isRollover() ? Color.RED : new Color(0x090909));

                    int delta = 5;
                    g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                    g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                    g2.dispose();
                }
            };

            closeButton.setPreferredSize(new Dimension(17, 17));
            //Make the button looks the same for all Laf's
            closeButton.setUI(new BasicButtonUI());
            //Make it transparent
            closeButton.setContentAreaFilled(false);
            //No need to be focusable
            closeButton.setFocusable(false);
            closeButton.setBorder(BorderFactory.createEtchedBorder());
            closeButton.setBorderPainted(false);

            setOpaque(false);
            titleLabel = new JLabel(title);
            add(titleLabel);
            add(closeButton);

            setFocusable(false);
        }

        public JButton getCloseButton() {
            return closeButton;
        }
        
        public JLabel getTitleLabel() {
            return titleLabel;
        }
    }

    @Override
    public void insertTab(final String title, Icon icon, final Component c, String tip, int index) {
        System.out.println("Inserting tab " + title);
        super.insertTab(title, icon, c, tip, index == -1 ? getTabCount() : index);
        getComponentAt(index);

        titles.put(index, title);

        CloseableTabComponent comp = new CloseableTabComponent(title);
        setTabComponentAt(index, comp);

        comp.getCloseButton().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (TabCloseListener listener : closers) {
                    if (!listener.tabClosed(title, c, CloseableTabbedPane.this))
                        return;
                }
                remove(c);
            }
        });
    }
}
