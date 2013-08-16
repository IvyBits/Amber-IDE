package amber.gl.model.obj;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

    protected String name;
    protected Material material;
    protected ArrayList<float[]> vertices = new ArrayList<float[]>();
    protected ArrayList<float[]> textureCoordinates = new ArrayList<float[]>();
    protected ArrayList<float[]> normals = new ArrayList<float[]>();
    protected ArrayList<Face> faces = new ArrayList<Face>();

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public void setName(String n) {
        name = n;
    }

    public void addVertex(float[] v) {
        vertices.add(v);
    }

    public void addTexCoord(float[] v) {
        textureCoordinates.add(v);
    }

    public void addNormal(float[] v) {
        normals.add(v);
    }

    public void addFace(Face f) {
        faces.add(f);
    }

    public String getName() {
        return name;
    }

    public List<float[]> getVerticies() {
        return vertices;
    }

    public List<float[]> getNormals() {
        return normals;
    }

    public List<float[]> getTexCoords() {
        return textureCoordinates;
    }

    public List<Face> getFaces() {
        return faces;
    }
}
