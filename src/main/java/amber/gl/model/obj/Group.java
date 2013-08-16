package amber.gl.model.obj;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;

public class Group {

    private String name;
    private Vector3f min = null;
    private Material material;
    private ArrayList<Face> faces = new ArrayList<Face>();
    public ArrayList<Integer> indices = new ArrayList<Integer>();
    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> texcoords = new ArrayList<Vector3f>();
    public int indexCount;

    public Group(String name) {
        indexCount = 0;
        this.name = name;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void addFace(Face face) {
        faces.add(face);
    }

    public void pack() {
        float minX = 0;
        float minY = 0;
        float minZ = 0;
        Face currentFace = null;
        Vector3f currentVertex = null;
        for (int i = 0; i < faces.size(); i++) {
            currentFace = faces.get(i);
            for (int j = 0; j < currentFace.getVertices().length; j++) {
                currentVertex = currentFace.getVertices()[j];
                if (Math.abs(currentVertex.getX()) > minX) {
                    minX = Math.abs(currentVertex.getX());
                }
                if (Math.abs(currentVertex.getY()) > minY) {
                    minY = Math.abs(currentVertex.getY());
                }
                if (Math.abs(currentVertex.getZ()) > minZ) {
                    minZ = Math.abs(currentVertex.getZ());
                }
            }
        }

        min = new Vector3f(minX, minY, minZ);
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public Vector3f getMin() {
        return min;
    }
}
