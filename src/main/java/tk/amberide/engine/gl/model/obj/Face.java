package tk.amberide.engine.gl.model.obj;

import org.lwjgl.util.vector.Vector3f;

public class Face {

    public static final int GL_TRIANGLES = 1;
    public static final int GL_QUADS = 2;
    public int[] vertIndices;
    public int[] normIndices;
    public int[] texIndices;
    private Vector3f[] vertices;
    private Vector3f[] normals;
    private Vector3f[] textures;
    private int type;

    public int[] getIndices() {
        return vertIndices;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setIndices(int[] indices) {
        this.vertIndices = indices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Vector3f[] getNormals() {
        return normals;
    }

    public void setNormals(Vector3f[] normals) {
        this.normals = normals;
    }

    public Vector3f[] getTextures() {
        return textures;
    }

    public void setTextures(Vector3f[] textures) {
        this.textures = textures;
    }
}
