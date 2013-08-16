package amber.gl;


import java.awt.image.BufferedImage;
import java.io.File;

import static org.lwjgl.opengl.GL11.*;

public class Sprite {
    private final Texture texture;
    private final int width;
    private final int height;

    public Sprite(String ref) {
        texture = TextureLoader.getTexture(ref);
        width = texture.getImageWidth();
        height = texture.getImageHeight();
    }

    public Sprite(BufferedImage ref) {
        texture = TextureLoader.getTexture(ref);
        width = texture.getImageWidth();
        height = texture.getImageHeight();
    }

    public Sprite(File ref) {
        texture = TextureLoader.getTexture(ref);
        width = texture.getImageWidth();
        height = texture.getImageHeight();
    }

    public int getWidth() {
        return texture.getImageWidth();
    }

    public int getHeight() {
        return texture.getImageHeight();
    }

    public void draw(float x, float y) {
        glPushMatrix();
        texture.bind();
        glTranslatef(x - texture.getImageWidth(), y, 0.0F);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.0F, 0.0F);
            glVertex2f(0.0F, height);
            glTexCoord2f(0.0F, texture.getHeight());
            glVertex2f(0.0F, 0.0F);
            glTexCoord2f(texture.getWidth(), texture.getHeight());
            glVertex2f(width, 0.0F);
            glTexCoord2f(texture.getWidth(), 0.0F);
            glVertex2f(width, height);
        }
        glEnd();
        glBindTexture(texture.getTarget(), 0);
        glPopMatrix();
    }
}