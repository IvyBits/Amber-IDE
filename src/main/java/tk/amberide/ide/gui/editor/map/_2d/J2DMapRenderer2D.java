package tk.amberide.ide.gui.editor.map._2d;

import tk.amberide.engine.data.map.Layer;
import tk.amberide.engine.data.map.LevelMap;
import tk.amberide.engine.data.map.Tile;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Tudor
 */
public class J2DMapRenderer2D {

    private LevelMap map;
    private int u = 32;
    private boolean grid;

    public J2DMapRenderer2D(LevelMap map) {
        this.map = map;
    }

    public void draw(Graphics g_) {
        Graphics2D g = (Graphics2D) g_;
        g.setColor(Color.WHITE);
        Rectangle clip = g.getClipBounds();
        g.fillRect(0, 0, clip.width, clip.height);

        int x1 = Math.max(0, clip.x / u - 1);
        int x2 = Math.min(x1 + clip.width / u + 3, map.getWidth());
        int y1 = Math.max(0, clip.y / u - 1);
        int y2 = Math.min(y1 + clip.height / u + 3, map.getLength());

        List<Layer> layers = map.getLayers();
        for (int i = 0; i != layers.size(); i++) {
            drawLayer(g, layers.get(i), x1, y1, x2, y2);
        }

        if (grid) {
            drawGrid(g, x1, y1, x2, y2);
        }
    }

    protected void drawLayer(Graphics2D g, Layer layer, int x1, int y1_, int x2, int y2_) {
        SparseVector<SparseMatrix<Tile>> tileVector = layer.tileMatrix();
        int y1 = map.getLength() - y2_;
        int y2 = map.getLength() - y1_;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                SparseVector.SparseVectorIterator iterator = tileVector.iterator();
                while (iterator.hasNext()) {
                    SparseMatrix<Tile> matrix = (SparseMatrix<Tile>) iterator.next();
                    Tile t = matrix.get(x, y);

                    if (t != null) {
                        Tileset.TileSprite sprite = t.getSprite();
                        Tileset sheet = sprite.getTileset();
                        BufferedImage texture = sheet.getImage();

                        Point start = sprite.getStart();
                        Dimension size = sprite.getSize();

                        int dx = x * u;
                        int dy = map.getLength() * u - y * u - u;

                        g.drawImage(texture, dx, dy, dx + u, dy + u, start.x, start.y, start.x + size.width, start.y + size.height, null);
                    }
                }
            }
        }
    }

    protected void drawGrid(Graphics2D g, int x1, int y1, int x2, int y2) {
        Color oldColor = g.getColor();
        g.setColor(Color.GRAY);

        Stroke oldStroke = g.getStroke();

        if (grid) {
            for (int x = x1; x <= x2; x++) {
                g.drawLine(x * u, 0, x * u, map.getLength() * u);
            }
            for (int y = y1; y <= y2; y++) {
                g.drawLine(0, y * u, map.getWidth() * u, y * u);
            }
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, 0, map.getWidth() * u, 0);
        g.drawLine(0, 0, 0, map.getLength() * u);
        g.drawLine(map.getWidth() * u, 0, map.getWidth() * u, map.getLength() * u);
        g.drawLine(0, map.getLength() * u, map.getWidth() * u, map.getLength() * u);

        g.setStroke(new BasicStroke(2));
        g.setColor(oldColor);
        g.setStroke(oldStroke);
    }

    public void setMap(LevelMap map) {
        this.map = map;
    }

    public LevelMap getMap() {
        return map;
    }

    public void zoomTo(int zoom) {
        u = zoom;
    }

    public int zoom() {
        return u;
    }

    public void drawGrid(boolean flag) {
        grid = flag;
    }

    public boolean drawingGrid() {
        return grid;
    }
}
