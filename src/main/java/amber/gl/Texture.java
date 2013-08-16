package amber.gl;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    protected int target;
    protected int textureID;
    protected int height;
    protected int width;
    protected int texWidth;
    protected int texHeight;
    protected float widthRatio;
    protected float heightRatio;
    protected boolean alpha;

    /**
     * Create a new texture
     *
     * @param target The GL target
     * @param textureID The GL texture ID
     */
    Texture(int target, int textureID, boolean alpha) {
        this.target = target;
        this.textureID = textureID;
        this.alpha = alpha;
    }

    public int getTarget() {
        return target;
    }

    /**
     * Bind the specified GL context to a texture
     */
    public void bind() {
        glBindTexture(target, textureID);
    }

    /**
     * Set the height of the image
     *
     * @param height The height of the image
     */
    public void setHeight(int height) {
        this.height = height;
        setHeight();
    }

    /**
     * Set the width of the image
     *
     * @param width The width of the image
     */
    public void setWidth(int width) {
        this.width = width;
        setWidth();
    }

    /**
     * Get the height of the original image
     *
     * @return The height of the original image
     */
    public int getImageHeight() {
        return height;
    }

    /**
     * Get the width of the original image
     *
     * @return The width of the original image
     */
    public int getImageWidth() {
        return width;
    }

    /**
     * Get the height of the physical texture
     *
     * @return The height of physical texture
     */
    public float getHeight() {
        return heightRatio;
    }

    /**
     * Get the width of the physical texture
     *
     * @return The width of physical texture
     */
    public float getWidth() {
        return widthRatio;
    }

    /**
     * Set the height of this texture
     *
     * @param texHeight The height of the texture
     */
    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        setHeight();
    }

    /**
     * Set the width of this texture
     *
     * @param texWidth The width of the texture
     */
    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        setWidth();
    }

    private void setHeight() {
        if (texHeight != 0) {
            heightRatio = ((float) height) / texHeight;
        }
    }

    private void setWidth() {
        if (texWidth != 0) {
            widthRatio = ((float) width) / texWidth;
        }
    }

    public int getID() {
        return textureID;
    }

    public boolean hasAlpha() {
        return alpha;
    }
}