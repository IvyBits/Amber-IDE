package amber.gl.atlas;

/**
 * Texture atlas interface.
 *
 * @author Tudor
 */
public interface ITextureAtlas {

    /**
     * Binds a segment of a texture with linear coordinates.
     *
     * @param x the x-coord of the region
     * @param y the y-coord of the region
     * @param w the width of the region
     * @param h the height of the region
     */
    void bindTextureRegion(int x, int y, int w, int h);

    /**
     * Unbinds a texture.
     */
    void unbind();

    /**
     * Specify a coordinate on the bound texture. Normalized coordinates.
     *
     * @param u the normalized x-coord of the region
     * @param v the normalized y-coord of the region
     */
    void atlasCoord(float u, float v);

    /**
     * If this implementation was using any sort of cache, it should be cleared.
     */
    void invalidate();
}