package tk.amberide.engine.data.map.codec;

import tk.amberide.Amber;
import tk.amberide.engine.data.map.*;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.ide.data.res.Tileset.TileSprite;
import tk.amberide.engine.data.io.ByteStream;

import tk.amberide.engine.data.map.exc.InvalidMapException;

import static tk.amberide.engine.data.map.codec.V1.Tag.*;

import tk.amberide.ide.data.res.Resource;
import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;
import tk.amberide.engine.gl.model.obj.WavefrontObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Tudor
 */
public class V1 extends Codec {

    public interface Tag {
        short MAP_MAGIC = 0x1337;
        int MAP_VERSION = 0x01;
        byte TAG_TILESHEET = 0x01;
        byte TAG_AUDIO = 0x02;
        byte TAG_MODEL = 0x03;
        byte MATRIX_SPARSE = 0x01;
        byte MATRIX_DENSE = 0x02;
    }

    @Override
    public LevelMap loadMap(DataInputStream buffer) throws IOException {
        if (buffer.readShort() != MAP_MAGIC) {
            throw new InvalidMapException("magic number != " + MAP_MAGIC);
        }
        int v;
        if ((v = buffer.readInt()) != MAP_VERSION)
            throw new InvalidMapException("wrong map version " + v);
        LevelMap map = new LevelMap(buffer.readShort(), buffer.readShort());

        HashMap<Short, Tileset.TileSprite> sprites = new HashMap<Short, Tileset.TileSprite>();
        HashMap<Short, WavefrontObject> models = new HashMap<Short, WavefrontObject>();
        int constantsCount = buffer.readShort();
        for (int i = 0; i != constantsCount; i++) {
            byte tag;
            switch (tag = buffer.readByte()) {
                case TAG_TILESHEET:
                    sprites.put(buffer.readShort(), TileSprite.byId(buffer.readUTF()));
                    break;
                case TAG_MODEL:
                    short id = buffer.readShort();
                    String name = buffer.readUTF();
                    for (Resource<WavefrontObject> e : Amber.getResourceManager().getModels()) {
                        if (e.getName().equals(name)) {
                            models.put(id, e.get());
                        }
                    }
                    break;
                case TAG_AUDIO:
                    throw new InvalidMapException("tag not implemented yet");
                default:
                    throw new InvalidMapException("invalid tag " + tag + " at index " + i);
            }
        }

        short layersCount = buffer.readShort();
        for (int i = 0; i != layersCount; i++) {
            String name = buffer.readUTF();
            Layer level = new Layer(name, map);
            int layerCnt = buffer.readInt();
            if (layerCnt == 0) {
                map.addLayer(level);
                continue;
            }
            for (int s = 0; s != layerCnt; s++) {
                byte matrix = buffer.readByte();
                int z = buffer.readInt();
                switch (matrix) {
                    case MATRIX_SPARSE:
                        int full = buffer.readInt();
                        for (int f = 0; f != full; f++) {
                            int x = buffer.readShort();
                            int y = buffer.readShort();
                            short id = buffer.readShort();
                            if (id > 0) {
                                Tile t = new Tile(sprites.get(id), Direction.values()[buffer.readByte()], Angle.values()[buffer.readByte()], TileType.values()[buffer.readByte()]);
                                level.setTile(x, y, z, t);
                            }
                        }
                        break;
                    case MATRIX_DENSE:
                        for (int x = 0; x != map.getWidth(); x++) {
                            for (int y = 0; y != map.getLength(); y++) {
                                short id = buffer.readShort();
                                if (id > 0) {
                                    Tile t = new Tile(sprites.get(id), Direction.values()[buffer.readByte()], Angle.values()[buffer.readByte()], TileType.values()[buffer.readByte()]);
                                    level.setTile(x, y, z, t);
                                }
                            }
                        }
                        break;
                    default:
                        System.err.println("invalid matrix type " + matrix + " at index " + s);
                        break;
                }
            }

            int modelCount = buffer.readInt();
            for (int m = 0; m != modelCount; m++) {
                level.setModel(buffer.readShort(),
                        buffer.readShort(),
                        buffer.readShort(),
                        new TileModel(models.get(buffer.readShort())));
            }
            map.addLayer(level);
        }
        return map;
    }

    @Override
    public void compileMap(LevelMap map, DataOutputStream out) throws IOException {
        ByteStream mapBuffer = ByteStream.writeStream();
        mapBuffer.writeShort(MAP_MAGIC);
        mapBuffer.writeInt(MAP_VERSION);
        mapBuffer.writeShort(map.getWidth());
        mapBuffer.writeShort(map.getLength());

        HashMap<Tileset.TileSprite, Integer> recordedTiles = new HashMap<Tileset.TileSprite, Integer>();
        HashMap<WavefrontObject, Integer> recordedModels = new HashMap<WavefrontObject, Integer>();

        int constantsCount = 0;
        ByteStream constantBuffer = ByteStream.writeStream();
        ByteStream mapLayerBuffer = ByteStream.writeStream();

        mapLayerBuffer.writeShort(map.getLayers().size());
        for (Layer layer : map.getLayers()) {
            ByteStream layerBuffer = ByteStream.writeStream();

            int totFull = 0;

            SparseVector.SparseVectorIterator tileIterator = layer.tileMatrix().iterator();
            while (tileIterator.hasNext()) {
                SparseMatrix<Tile> matrix = (SparseMatrix<Tile>) tileIterator.next();
                // This is a two step process. First, we calculate the amount of non-null
                // tiles in the current plane.
                // We can also use this phase to compile tile IDs
                int full = 0;
                for (int x = 0; x != map.getWidth(); x++) {
                    for (int y = 0; y != map.getLength(); y++) {
                        Tile t = matrix.get(x, y);
                        if (t != null) {
                            full++;
                            Tileset.TileSprite spr = t.getSprite();
                            // This is not a recorded tile, so we should save it.
                            if (!recordedTiles.containsKey(spr)) {
                                constantBuffer.writeByte(TAG_TILESHEET).writeShort(recordedTiles.size() + 1).writeUTF(spr.getId());
                                recordedTiles.put(spr, recordedTiles.size() + 1);
                                constantsCount++;
                            }
                        }
                    }
                }

                if (full == 0) {
                    // There are no tiles in this plane, it can be ignored
                    continue;
                }
                totFull++;

                int z = tileIterator.realIndex();

                // Now we fetch the total amount of cells on the current plane.
                int totalTiles = map.getWidth() * map.getLength();
                // To store in a sparse format, we need 3 pieces of information:
                // * tile id
                // * x coordinate
                // * y coordinate
                // To store in a solid block, we need only the id, but we need to fill
                // the entire block. Assuming tile id, x-coord and y-coord
                // are of the same datatype, we can check whether it is worthwhile to save
                // in a sparce format with a simple evaluation, below.
                boolean sparse = totalTiles > full * 3;
                // We need to identify this plane as being sparse or block based.
                // We also need to write the plane z-coord, in case a layer was
                // skipped, or the format is not in sequential order.

                layerBuffer.writeByte(sparse ? MATRIX_SPARSE : MATRIX_DENSE);
                layerBuffer.writeInt(z);

                if (sparse) {
                    layerBuffer.writeInt(full);
                    for (int x = 0; x != map.getWidth(); x++) {
                        for (int y = 0; y != map.getLength(); y++) {
                            Tile t = layer.getTile(x, y, z);
                            int id = t != null ? recordedTiles.get(t.getSprite()) : 0;
                            if (id > 0) {
                                layerBuffer.writeShort(x)
                                        .writeShort(y)
                                        .writeShort(id);
                                layerBuffer.writeByte(t.getDirection().ordinal());
                                layerBuffer.writeByte(t.getAngle().ordinal());
                                layerBuffer.writeByte(t.getType().ordinal());
                            }
                        }
                    }
                } else {
                    // Iterate over every row and save each tile id
                    // sequentially.
                    for (int x = 0; x != map.getWidth(); x++) {
                        for (int y = 0; y != map.getLength(); y++) {
                            Tile t = layer.getTile(x, y, z);
                            int id = t != null ? recordedTiles.get(t.getSprite()) : 0;
                            layerBuffer.writeShort(id);
                            if (id > 0) {
                                layerBuffer.writeByte(t.getDirection().ordinal());
                                layerBuffer.writeByte(t.getAngle().ordinal());
                                layerBuffer.writeByte(t.getType().ordinal());
                            }
                        }
                    }
                }
            }

            int nz = 0;
            for (SparseMatrix<TileModel> matrix : layer.modelMatrix()) {
                for (TileModel u : matrix) {
                    if (u != null) {
                        nz++;
                    }
                }
            }
            layerBuffer.writeInt(nz);
            SparseVector.SparseVectorIterator modelIterator = layer.modelMatrix().iterator();
            HashMap<WavefrontObject, String> modelIds = new HashMap<WavefrontObject, String>();
            for (Resource<WavefrontObject> models : Amber.getResourceManager().getModels()) {
                modelIds.put(models.get(), models.getName());
            }
            while (modelIterator.hasNext()) {
                SparseMatrix.SparseMatrixIterator matrixIterator = ((SparseMatrix<WavefrontObject>) modelIterator.next()).iterator();
                while (matrixIterator.hasNext()) {
                    TileModel tile = (TileModel) matrixIterator.next();
                    if (tile != null) {
                        WavefrontObject obj = tile.getModel();
                        if (!recordedModels.containsKey(obj)) {
                            if (modelIds.containsKey(obj)) {
                                constantBuffer.writeByte(TAG_MODEL).writeShort(recordedModels.size() + 1).writeUTF(modelIds.get(obj));
                                recordedModels.put(obj, recordedModels.size() + 1);
                                constantsCount++;
                            }
                        }
                        layerBuffer.writeShort(matrixIterator.realX())
                                .writeShort(matrixIterator.realY())
                                .writeShort(modelIterator.realIndex()) // z
                                .writeShort(recordedModels.get(obj));
                    }
                }
            }

            mapLayerBuffer.writeUTF(layer.getName());
            mapLayerBuffer.writeInt(totFull);
            System.out.println(totFull);
            mapLayerBuffer.writeBytes(layerBuffer.getBuffer());
        }

        mapBuffer.writeShort(constantsCount);
        mapBuffer.writeBytes(constantBuffer.getBuffer());
        mapBuffer.writeBytes(mapLayerBuffer.getBuffer());

        out.write(mapBuffer.getBuffer());
    }

}
