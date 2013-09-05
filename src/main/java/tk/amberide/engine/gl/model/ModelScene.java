package tk.amberide.engine.gl.model;

import tk.amberide.engine.gl.model.obj.Mesh;
import tk.amberide.engine.gl.Buffers;
import tk.amberide.engine.gl.TextureLoader;
import java.io.File;
import java.util.ArrayList;

import tk.amberide.engine.gl.model.obj.Face;
import tk.amberide.engine.gl.model.obj.Group;
import tk.amberide.engine.gl.model.obj.Material;
import tk.amberide.engine.gl.model.obj.WavefrontObject;
import java.io.IOException;
import java.util.List;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class ModelScene {

    protected final WeakHashMap<Material, Integer> textureCache = new WeakHashMap<Material, Integer>();
    protected float sx = 1, sy = 1, sz = 1;

    public ModelScene(File file) throws IOException {
        this(file, 1, 1, 1);
    }

    public ModelScene(File file, float s) throws IOException {
        this(file, s, s, s);
    }

    public ModelScene(File file, float sx, float sy, float sz) throws IOException {
        this(new WavefrontObject(file), sx, sy, sz);
    }

    public ModelScene(WavefrontObject obj, float s) throws IOException {
        this(obj, s, s, s);
    }

    public ModelScene(WavefrontObject obj) throws IOException {
        this(obj, 1, 1, 1);
    }

    public ModelScene(WavefrontObject obj, float sx, float sy, float sz) throws IOException {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        displayListId = glGenLists(1);
        model = obj;
        ArrayList<Group> groups = model.getGroups();
        for (int gi = 0;
                gi < groups.size();
                gi++) {
            Group g = groups.get(gi);
            Material gm = g.getMaterial();

            Mesh mesh = new Mesh();
            mesh.setName(g.getName());
            mesh.setMaterial(gm);

            String texName = gm.getTextureName();
            if (texName != null && texName.length() > 0) {
                gm.setTexture(ImageIO.read(new File(obj.getObjectFile().getParentFile(), texName)));
            }

            for (int fi = 0; fi < g.getFaces().size(); fi++) {
                mesh.addFace(g.getFaces().get(fi));
            }

            for (int vi = 0; vi < model.getVertices().size(); vi++) {
                Vector3f v = (Vector3f) model.getVertices().get(vi);
                vertices.add(new float[]{v.getX(), v.getY(), v.getZ()});
            }

            for (int vi = 0; vi < model.getNormals().size(); vi++) {
                Vector3f v = (Vector3f) model.getNormals().get(vi);
                normals.add(new float[]{v.getX(), v.getY(), v.getZ()});
            }

            for (int vi = 0; vi < model.getTextures().size(); vi++) {
                Vector3f tc = (Vector3f) model.getTextures().get(vi);
                textures.add(new float[]{tc.x, tc.y, tc.z});
            }

            addMesh(mesh);
        }
    }

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void draw() {
        // If the list is compiled and everything is ok, render
        if (displayListId > 0 && listCompiled) {
            glCallList(displayListId);
            return;
        }

        if (displayListId > 0 && !listCompiled) {
            glNewList(displayListId, GL_COMPILE);
        }

        for (int i = 0; i < meshes.size(); i++) {
            Mesh m = meshes.get(i);
            Material mat = m.getMaterial();
            int texId;
            if (textureCache.containsKey(mat)) {
                texId = textureCache.get(mat);
            } else {
                textureCache.put(mat, texId = TextureLoader.getTexture(mat.getTexture()).getID());
            }

            // If current material has texture, bind it
            if (texId > 0) {
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, texId);
            } else {
                glBindTexture(GL_TEXTURE_2D, 0);
                glDisable(GL_TEXTURE_2D);
            }
            Vector3f ka = mat.getKa();
            glMaterial(GL_FRONT_AND_BACK, GL_AMBIENT, Buffers.asFlippedFloatBuffer(new float[]{ka.x, ka.y, ka.z, 1.0f}));
            Vector3f kd = mat.getKd();
            glMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE, Buffers.asFlippedFloatBuffer(new float[]{kd.x, kd.y, kd.z, 1.0f}));
            Vector3f ks = mat.getKs();
            if (ks != null) {
                glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, Buffers.asFlippedFloatBuffer(new float[]{ks.x, ks.y, ks.z, 1.0f}));
            }
            glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, mat.getShininess());
            //glEnable( GL_COLOR_MATERIAL );

            // render triangles.. this is too basic. should be optimized
            glBegin(GL_TRIANGLES);

            //if( m._material._texId > 0 )
            //glColor4f( 1, 1, 1, 1 );
            //else
            glColor4f(kd.x, kd.y, kd.z, 1.0f);
            List<Face> faces = m.getFaces();
            for (int fi = 0; fi < faces.size(); fi++) {
                Face f = faces.get(fi);

                glNormal3f(normals.get(f.normIndices[0])[0], normals.get(f.normIndices[0])[1], normals.get(f.normIndices[0])[2]);
                if (texId > 0) {
                    glTexCoord2f(textures.get(f.texIndices[0])[0], textures.get(f.texIndices[0])[1]);
                }
                glVertex3f(vertices.get(f.vertIndices[0])[0] * sx, vertices.get(f.vertIndices[0])[1] * sy, vertices.get(f.vertIndices[0])[2] * sz);

                glNormal3f(normals.get(f.normIndices[1])[0], normals.get(f.normIndices[1])[1], normals.get(f.normIndices[1])[2]);
                if (texId > 0) {
                    glTexCoord2f(textures.get(f.texIndices[1])[0], textures.get(f.texIndices[1])[1]);
                }
                glVertex3f(vertices.get(f.vertIndices[1])[0] * sx, vertices.get(f.vertIndices[1])[1] * sy, vertices.get(f.vertIndices[1])[2] * sz);

                glNormal3f(normals.get(f.normIndices[2])[0], normals.get(f.normIndices[2])[1], normals.get(f.normIndices[2])[2]);
                if (texId > 0) {
                    glTexCoord2f(textures.get(f.texIndices[2])[0], textures.get(f.texIndices[2])[1]);
                }
                glVertex3f(vertices.get(f.vertIndices[2])[0] * sx, vertices.get(f.vertIndices[2])[1] * sy, vertices.get(f.vertIndices[2])[2] * sz);
            }

            glEnd();
        }

        if (displayListId > 0 && !listCompiled) {
            glEndList();
            listCompiled = true;
        }
    }
    int displayListId;
    boolean listCompiled;
    ArrayList<float[]> vertices = new ArrayList<float[]>();
    ArrayList<float[]> normals = new ArrayList<float[]>();
    ArrayList<float[]> textures = new ArrayList<float[]>();
    ArrayList<Mesh> meshes = new ArrayList<Mesh>();
    private WavefrontObject model;
}
