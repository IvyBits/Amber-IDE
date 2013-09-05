package tk.amberide.engine.gl.model.obj;

import java.awt.image.BufferedImage;
import org.lwjgl.util.vector.Vector3f;

public class Material {

    protected BufferedImage texture;
    protected Vector3f Ka;
    protected Vector3f Kd;
    protected Vector3f Ks;
    protected float shininess;
    protected String name;
    protected String texName;

    public Material(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getTextureName() {
        return texName;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public Vector3f getKa() {
        return Ka;
    }

    public Vector3f getKd() {
        return Kd;
    }

    public Vector3f getKs() {
        return Ks;
    }

    public float getShininess() {
        return shininess;
    }

    public void setKa(Vector3f ka) {
        Ka = ka;
    }

    public void setKd(Vector3f kd) {
        Kd = kd;
    }

    public void setKs(Vector3f ks) {
        Ks = ks;
    }

    public void setShininess(float s) {
        shininess = s;
    }
}
