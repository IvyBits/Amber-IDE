package tk.amberide.ide.gui.viewer;

import tk.amberide.engine.gl.FrameTimer;
import tk.amberide.ide.gui.FileViewerPanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Tudor
 */
public class ImageViewerPanel extends FileViewerPanel {

    protected class ImageViewer extends JComponent {
        BufferedImage image;
        double u = 1;
        JScrollPane container;
        final ImageViewerPanel owner;

        public ImageViewer(ImageViewerPanel owner, File file) throws IOException {
            this.owner = owner;
            image = ImageIO.read(file);
            container = new JScrollPane(this);
            updateSize();
            container.getHorizontalScrollBar().setUnitIncrement(16);
            container.getVerticalScrollBar().setUnitIncrement(16);

            MouseAdapter adapter = new MouseAdapter() {
                int lx, ly;

                @Override
                public void mousePressed(MouseEvent e) {
                    lx = e.getXOnScreen();
                    ly = e.getYOnScreen();
                    if (container.getWidth() < imageWidth() || container.getHeight() < imageHeight())
                        container.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    requestFocusInWindow();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    moveImage(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    moveImage(e);
                    container.setCursor(Cursor.getDefaultCursor());
                }

                void moveImage(MouseEvent e) {
                    if (container.getWidth() < imageWidth()) {
                        int dx = lx - e.getXOnScreen();
                        container.getHorizontalScrollBar().setValue(container.getHorizontalScrollBar().getValue() + dx);
                    }
                    if (container.getHeight() < imageHeight()) {
                        int dy = ly - e.getYOnScreen();
                        container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue() + dy);
                    }
                    lx = e.getXOnScreen();
                    ly = e.getYOnScreen();
                }
            };
            addMouseMotionListener(adapter);
            addMouseListener(adapter);
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.isControlDown()) {
                        double multiplier = e.getUnitsToScroll() < 0 ? 1.3 : 1.0/1.3;
                        u *= multiplier;
                        updateZoom(multiplier);
                    }
                }
            });

            addKeyListener(new KeyAdapter() {
                protected FrameTimer timer = new FrameTimer();

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getModifiersEx() == 0) {
                        int delta = Math.min(timer.getDelta() / 5, 200);
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_W:
                            case KeyEvent.VK_UP:
                                container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue() - delta);
                                break;
                            case KeyEvent.VK_S:
                            case KeyEvent.VK_DOWN:
                                container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue() + delta);
                                break;
                            case KeyEvent.VK_A:
                            case KeyEvent.VK_LEFT:
                                container.getHorizontalScrollBar().setValue(container.getHorizontalScrollBar().getValue() - delta);
                                break;
                            case KeyEvent.VK_D:
                            case KeyEvent.VK_RIGHT:
                                container.getHorizontalScrollBar().setValue(container.getHorizontalScrollBar().getValue() + delta);
                                break;
                        }
                    } else if (e.isControlDown()) {
                        double multiplier;
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_EQUALS:
                            case KeyEvent.VK_PLUS:
                            case KeyEvent.VK_ADD:
                                multiplier = 1.3;
                                u *= multiplier;
                                updateZoom(multiplier);
                                break;
                            case KeyEvent.VK_MINUS:
                            case KeyEvent.VK_SUBTRACT:
                                multiplier = 1./1.3;
                                u *= multiplier;
                                updateZoom(multiplier);
                                break;
                        }
                    }
                }
            });

            updateStatus();
        }

        private void updateZoom(double multiplier) {
            JScrollBar sx = container.getHorizontalScrollBar(), sy = container.getVerticalScrollBar();
            int vx = sx.getVisibleAmount() / 2, vy = sy.getVisibleAmount() / 2;
            updateSize();

            repaint();
            container.revalidate();
            sx.setValue((int) ((sx.getValue() + vx) * multiplier) - sx.getVisibleAmount() / 2);
            sy.setValue((int) ((sy.getValue() + vy) * multiplier) - sx.getVisibleAmount() / 2);

            updateStatus();
        }

        private void updateStatus() {
            owner.status.setText(String.format("Image Size: %dx%d, Scaled Size: %dx%d (%.2f%%)", image.getWidth(),
                    image.getHeight(), imageWidth(), imageHeight(), u * 100));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, imageWidth(), imageHeight(), null);
        }

        public int imageWidth() {
            return (int) (image.getWidth() * u);
        }

        public int imageHeight() {
            return (int) (image.getHeight() * u);
        }

        protected void updateSize() {
            Dimension size = new Dimension(imageWidth(), imageHeight());
            setPreferredSize(size);
            container.setPreferredSize(size);
            container.updateUI();
        }

        public JComponent getContainer() {
            return container;
        }
    }

    ImageViewer viewer;
    JLabel status = new JLabel("Loading image...");

    public ImageViewerPanel(File file) {
        super(file);
        setLayout(new BorderLayout());
        try {
            add((viewer = new ImageViewer(this, file)).getContainer(), BorderLayout.CENTER);
        } catch (Exception e) {
            add(wrap(new JLabel("Failed to load image.")), BorderLayout.CENTER);
            e.printStackTrace();
            status.setText("Failed to load image: " + e.getMessage());
        }
    }

    protected JScrollPane wrap(JComponent component) {
        JScrollPane pane = new JScrollPane(component);
        pane.getHorizontalScrollBar().setUnitIncrement(16);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        return pane;
    }

    @Override
    public JMenu[] getContextMenus() {
        return new JMenu[0];
    }

    @Override
    public JComponent getStatusBar() {
        return status;
    }
}
