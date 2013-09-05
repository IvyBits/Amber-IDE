package tk.amberide.ide.gui.editor.map;

import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.data.map.Flag;
import tk.amberide.engine.data.map.LevelMap;
import tk.amberide.engine.gl.model.obj.WavefrontObject;

import java.awt.*;
import java.io.File;
import java.util.Stack;

/**
 *
 * @author Tudor
 */
public class MapContext {

    public static final int TYPE_TILE = 0,
            TYPE_FLAG = 1,
            EXT_TYPE_MODEL = 2;
    public static final int MODE_BRUSH = 0,
            MODE_FILL = 1,
            MODE_SELECT = 2,
            MODE_MOVE = 3,
            MODE_ERASE = 4;
    public LevelMap map;
    public File outputFile;
    public Tileset.TileSprite[][] tileSelection;
    public Flag flag;
    public int drawType;
    public int drawMode;
    public int layer;
    public Stack<LevelMap> undoStack = new Stack<LevelMap>();
    public Stack<LevelMap> redoStack = new Stack<LevelMap>();
    public WavefrontObject EXT_modelSelection;
    public boolean EXT_modelSelectionSupported;
    public boolean EXT_cardinal = true;
    public boolean EXT_cardinalSupported;
    public Rectangle selection;
}
