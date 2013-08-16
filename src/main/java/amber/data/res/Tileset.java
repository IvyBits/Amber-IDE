package amber.data.res;

import amber.Amber;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

/**
 * @author Tudor
 */
public class Tileset {

    private BufferedImage image;
    private Dimension tileSize;
    private int margin, spacing;
    private TileSprite[][] tiles;

    Tileset(BufferedImage image, Dimension tileSize, int margin, int spacing) {
        this.image = image;
        this.tileSize = tileSize;
        this.margin = margin;
        this.spacing = spacing;
    }

    public TileSprite getTile(int x, int y) {
        return tiles[x][y];
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * @return the tileSize
     */
    public Dimension getTileSize() {
        return tileSize;
    }

    /**
     * @return the margin
     */
    public int getMargin() {
        return margin;
    }

    /**
     * @return the spacing
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * @return the tiles
     */
    public TileSprite[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public static class Parser {

        private Dimension tileSize;
        private int margin;
        private int spacing;

        public Parser(Dimension tileSize, int margin, int spacing) {
            assert tileSize != null && tileSize.width > 0 && tileSize.height > 0 : "invalid dimensions";
            assert margin >= 0 : "margin must not be negative";
            assert spacing >= 0 : "spacing must not be negative";

            this.tileSize = tileSize;
            this.margin = margin;
            this.spacing = spacing;
        }

        public Tileset parse(BufferedImage img) {
            int tilesAcross = ((img.getWidth() - (margin * 2) - tileSize.width) / (tileSize.width + spacing)) + 2;
            int tilesDown = ((img.getHeight() - (margin * 2) - tileSize.height) / (tileSize.height + spacing)) + 2;
            if ((img.getHeight() - tileSize.height) % (tileSize.height + spacing) != 0) {
                tilesDown++;
            }
            Tileset out = new Tileset(img, tileSize, margin, spacing);
            TileSprite[][] subImages = new TileSprite[tilesAcross][tilesDown];
            for (int x = 0; x < tilesAcross; x++) {
                for (int y = 0; y < tilesDown; y++) {
                    subImages[x][y] = new TileSprite(
                            new Point(
                            x * (tileSize.width + spacing) + margin,
                            y * (tileSize.height + spacing) + margin),
                            tileSize,
                            out);
                }
            }
            out.tiles = subImages;
            return out;
        }
    }

    public static class TileSprite {

        private Point start;
        private Dimension size;
        private Tileset tileset;
        public static final Tileset.TileSprite NULL_SPRITE = new Tileset.TileSprite(null, null, null);

        public TileSprite(Point start, Dimension size, Tileset tileset) {
            this.start = start;
            this.size = size;
            this.tileset = tileset;
        }

        public Dimension getSize() {
            return size;
        }

        /**
         * @return the start
         */
        public Point getStart() {
            return start;
        }

        /**
         * @param start the start to set
         */
        public void setStart(Point start) {
            this.start = start;
        }

        /**
         * @return the owner
         */
        public Tileset getTileset() {
            return tileset;
        }

        /**
         * @param owner the owner to set
         */
        public void setTileset(Tileset tileset) {
            this.tileset = tileset;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TileSprite) {
                TileSprite other = (TileSprite) o;
                return (other.start == null && start == null
                        && other.size == null && size == null
                        && other.tileset == null && tileset == null) || (other.start.equals(start) && other.size.equals(size) && other.tileset.equals(tileset));
            }
            return false;
        }

        public String getId() {
            String base = null;

            for (Resource<Tileset> res : Amber.getResourceManager().getTilesets()) {
                if (tileset.equals(res.get())) {
                    base = res.getName();
                    break;
                }
            }

            return base + "|" + tileString(this);
        }

        public static TileSprite byId(String id) {
            String base = id.split("\\|")[0];
            Collection<Resource<Tileset>> sheets = Amber.getResourceManager().getTilesets();
            for (Resource<Tileset> res : sheets) {
                if (res.getName().equals(base)) {
                    Tileset sheet = res.get();
                    TileSprite[][] sprites = sheet.tiles;
                    String hash = id.split("\\|")[1];
                    for (TileSprite[] column : sprites) {
                        for (TileSprite sprite : column) {
                            if (hash.equals(tileString(sprite))) {
                                return sprite;
                            }
                        }
                    }
                }
            }
            return null;
        }

        protected static final String tileString(TileSprite sprite) {
            return String.format("%s,%s,%s,%s", sprite.start.x, sprite.start.y, sprite.size.width, sprite.size.height);
        }
    }
}