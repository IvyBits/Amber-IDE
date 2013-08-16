package amber.gl.model.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import org.lwjgl.util.vector.Vector3f;

public class WavefrontObject {

    private ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    private ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    private ArrayList<Vector3f> textures = new ArrayList<Vector3f>();
    //private ArrayList<Face> faces = new ArrayList<Face>();
    private ArrayList<Group> groups = new ArrayList<Group>();
    private HashMap<String, Group> groupsDirectAccess = new HashMap<String, Group>();
    HashMap<String, Material> materials = new HashMap<String, Material>();
    private File context;
    private File mtllib;
    private Material currentMaterial;
    private Group currentGroup;

    public WavefrontObject(File fileName) throws FileNotFoundException {
        this.context = fileName;

        parse(fileName);
    }

    protected void parse(File context) throws FileNotFoundException {
        InputStream fileInput = new FileInputStream(context);
        BufferedReader in;

        try {
            in = new BufferedReader(new InputStreamReader(fileInput));


            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                currentLine = currentLine.trim();
                if (currentLine.isEmpty() || currentLine.startsWith("#")) {
                    continue;
                }
                String[] words = currentLine.split(" ");
                String type = words[0];
                if ("v".equals(type)) {
                    vertices.add(new Vector3f(
                            (Float.parseFloat(words[1])),
                            (Float.parseFloat(words[2])),
                            (Float.parseFloat(words[3]))));
                } else if ("vn".equals(type)) {
                    normals.add(new Vector3f(Float.parseFloat(words[1]),
                            Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                } else if ("vt".equals(type)) {
                    Vector3f coordinate = new Vector3f();
                    if (words.length >= 2) {
                        coordinate.x = Float.parseFloat(words[1]);
                    }
                    if (words.length >= 3) {
                        coordinate.y = 1 - Float.parseFloat(words[2]); // OBJ origin is at upper left, OpenGL origin is at lower left.
                    }
                    if (words.length >= 4) {
                        coordinate.z = Float.parseFloat(words[3]);
                    }
                    textures.add(coordinate);
                } else if ("f".equals(type)) {
                    Face face = new Face();
                    Vector3f[] normals = new Vector3f[3];
                    Vector3f[] textures = new Vector3f[3];
                    int vertexCount = -1;
                    if (words.length == 4) {
                        face.setType(Face.GL_TRIANGLES);
                        vertexCount = 3;
                    } else if (words.length == 4) {
                        face.setType(Face.GL_QUADS);
                        vertexCount = 4;
                    }

                    String[] rawFaces;
                    int currentValue;
                    int[] vindices = new int[vertexCount];
                    int[] nindices = new int[vertexCount];
                    int[] tindices = new int[vertexCount];
                    Vector3f[] vertices = new Vector3f[vertexCount];
                    for (int i = 1; i <= vertexCount; i++) {
                        rawFaces = words[i].split("/");
                        // v
                        currentValue = Integer.parseInt(rawFaces[0]);
                        vindices[i - 1] = currentValue - 1;
                        // save vertex
                        vertices[i - 1] = getVertices().get(currentValue - 1);	// -1 because references starts at 1

                        if (rawFaces.length == 1) {
                            continue;
                        }

                        if (!rawFaces[1].isEmpty()) {
                            currentValue = Integer.parseInt(rawFaces[1]);
                            if (currentValue <= getTextures().size()) // This is to compensate the fact that if no texture is in the obj file, sometimes '1' is put instead of 'blank' (we find coord1/1/coord3 instead of coord1//coord3 or coord1/coord3)
                            {
                                tindices[i - 1] = currentValue - 1;
                                textures[i - 1] = getTextures().get(currentValue - 1); // -1 because references starts at 1
                            }
                        }

                        // save normal
                        currentValue = Integer.parseInt(rawFaces[2]);
                        nindices[i - 1] = currentValue - 1;
                        normals[i - 1] = getNormals().get(currentValue - 1); 	// -1 because references starts at 1
                    }

                    if (currentGroup == null) {
                        currentGroup = new Group(String.valueOf(System.nanoTime() + new Random().nextInt()));
                        getGroups().add(currentGroup);
                        getGroupsDirectAccess().put(currentGroup.getName(), currentGroup);
                        setCurrentGroup(currentGroup);
                    }

                    // Add list of vertex/normal/texcoord to current group
                    // Each object keeps a list of its own data, apart from the global list
                    currentGroup.vertices.add(vertices[0]);
                    currentGroup.vertices.add(vertices[1]);
                    currentGroup.vertices.add(vertices[2]);
                    currentGroup.normals.add(normals[0]);
                    currentGroup.normals.add(normals[1]);
                    currentGroup.normals.add(normals[2]);
                    currentGroup.texcoords.add(textures[0]);
                    currentGroup.texcoords.add(textures[1]);
                    currentGroup.texcoords.add(textures[2]);
                    currentGroup.indices.add(currentGroup.indexCount++);
                    currentGroup.indices.add(currentGroup.indexCount++);
                    currentGroup.indices.add(currentGroup.indexCount++);	// create index list for current object

                    face.vertIndices = vindices;
                    face.normIndices = nindices;
                    face.texIndices = tindices;
                    face.setNormals(normals);
                    face.setNormals(normals);
                    face.setVertices(vertices);
                    face.setTextures(textures);

                    getCurrentGroup().addFace(face);
                } else if ("mtllib".equals(type)) {
                    mtllib = new File(context.getParentFile(), words[1]);
                    BufferedReader mtlin = new BufferedReader(new InputStreamReader(new FileInputStream(mtllib)));

                    String mtlline;
                    while ((mtlline = mtlin.readLine()) != null) {
                        mtlline = mtlline.trim();
                        if (mtlline.isEmpty() || currentLine.startsWith("#")) {
                            continue;
                        }

                        String[] mwords = mtlline.split(" ");
                        String mtltype = mwords[0];

                        if ("newmtl".equals(mtltype)) {
                            Material newMaterial = new Material(mwords[1]);
                            materials.put(mwords[1], newMaterial);
                            currentMaterial = newMaterial;
                        } else if ("Ka".equals(mtltype)) {
                            currentMaterial.setKa(new Vector3f(Float.parseFloat(mwords[1]),
                                    Float.parseFloat(mwords[2]), Float.parseFloat(mwords[3])));
                        } else if ("Kd".equals(mtltype)) {
                            currentMaterial.setKd(new Vector3f(Float.parseFloat(mwords[1]),
                                    Float.parseFloat(mwords[2]), Float.parseFloat(mwords[3])));
                        } else if ("Ks".equals(mtltype)) {
                            currentMaterial.setKs(new Vector3f(Float.parseFloat(mwords[1]),
                                    Float.parseFloat(mwords[2]), Float.parseFloat(mwords[3])));
                        } else if ("Ns".equals(mtltype)) {
                            currentMaterial.setShininess(Float.parseFloat(mwords[1]));
                        } else if ("map_Kd".equals(mtltype)) {
                            String texName = mwords[mwords.length - 1];
                            currentMaterial.texName = texName;
                            currentMaterial.setTexture(ImageIO.read(new File(context.getParentFile(), texName)));
                        }
                    }
                    mtlin.close();
                } else if ("usemtl".equals(type)) {
                    currentGroup.setMaterial(materials.get(words[1]));
                } else if ("g".equals(type)) {
                    String groupName = words[1];
                    Group newGroup = new Group(groupName);

                    if (currentGroup != null) {
                        currentGroup.pack();
                    }

                    groups.add(newGroup);
                    groupsDirectAccess.put(newGroup.getName(), newGroup);
                    currentGroup = newGroup;
                }
            }

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error reading file :'" + context + "'");
        }
    }

    public File getMaterialsFile() {
        return mtllib;
    }

    public File getObjectFile() {
        return context;
    }

    public void setMaterials(HashMap<String, Material> materials) {
        this.materials = materials;
    }

    public void setTextures(ArrayList<Vector3f> textures) {
        this.textures = textures;
    }

    public ArrayList<Vector3f> getTextures() {
        return textures != null ? textures : new ArrayList<Vector3f>();
    }

    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Vector3f> getVertices() {
        return vertices != null ? vertices : new ArrayList<Vector3f>();
    }

    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals != null ? normals : new ArrayList<Vector3f>();
    }

    public HashMap<String, Material> getMaterials() {
        return materials != null ? materials : new HashMap<String, Material>();
    }

    public Material getCurrentMaterial() {
        return currentMaterial;
    }

    public void setCurrentMaterial(Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    public ArrayList<Group> getGroups() {
        return groups != null ? groups : new ArrayList<Group>();
    }

    public HashMap<String, Group> getGroupsDirectAccess() {
        return groupsDirectAccess;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }
}
