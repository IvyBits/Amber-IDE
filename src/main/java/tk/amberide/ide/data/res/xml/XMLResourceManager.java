package tk.amberide.ide.data.res.xml;

import tk.amberide.engine.al.Audio;
import tk.amberide.engine.al.AudioIO;
import tk.amberide.ide.data.Workspace;
import tk.amberide.ide.data.io.FileIO;
import tk.amberide.ide.data.res.AbstractResourceManager;
import tk.amberide.ide.data.res.Resource;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.gl.model.obj.WavefrontObject;
import tk.amberide.ide.gui.misc.ErrorHandler;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import static java.lang.String.valueOf;
import static java.lang.Integer.parseInt;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Serializer;

/**
 * @author Tudor
 */
public class XMLResourceManager extends AbstractResourceManager {

    /**
     * Constructs a new XMLResourceManager object for the given Workspace
     *
     * @param space the Workspace
     */
    public XMLResourceManager(Workspace space) {
        super(space);
    }

    public void loadResources() throws Exception {
        try {
            File resourceFile = new File(workspace.getDataDirectory(), "resources.xml");
            Document doc = new Builder().build(resourceFile);
            Element root = doc.getRootElement();

            Elements tileNodes = root.getChildElements("tilesets").get(0).getChildElements();
            for (int i = 0; i < tileNodes.size(); i++) {
                Element sheet = tileNodes.get(i);
                String name = sheet.getAttributeValue("name");
                try {
                    File source = FileIO.getFilesByName(name, imgDir)[0];
                    BufferedImage img;
                    try {
                        img = ImageIO.read(source);
                    } catch (Exception e) {
                        System.err.println("failed to read " + name);
                        e.printStackTrace();
                        continue;
                    }
                    importTileset(name, new Tileset.Parser(
                            new Dimension(
                            parseInt(sheet.getAttributeValue("tile-width")),
                            parseInt(sheet.getAttributeValue("tile-height"))),
                            parseInt(sheet.getAttributeValue("margin")),
                            parseInt(sheet.getAttributeValue("spacing")))
                            .parse(img), source);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Elements audioNodes = root.getChildElements("audio").get(0).getChildElements();
            for (int i = 0; i != audioNodes.size(); i++) {
                String name = audioNodes.get(i).getAttributeValue("name");
                try {
                    File source = FileIO.getFilesByName(name, audioDir)[0];
                    importAudio(name, AudioIO.read(source), source);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Elements modelNodes = root.getChildElements("models").get(0).getChildElements();
            for (int i = 0; i != modelNodes.size(); i++) {
                String name = modelNodes.get(i).getAttributeValue("name");
                try {
                    File source = new File(modelDir, name + File.separator + name + ".obj");
                    System.out.println("Loaded model " + source);
                    importModel(name, new WavefrontObject(source), source);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void emitResources() {
        File resourceFile = new File(workspace.getDataDirectory(), "resources.xml");
        try {
            resourceFile.delete(); // Clear file
            resourceFile.createNewFile();
            Element root = new Element("resources");
            Document doc = new Document(root);

            Element sheets = new Element("tilesets");
            for (Resource<Tileset> set : getTilesets()) {
                Element node = new Element("tileset");
                node.addAttribute(new Attribute("name", set.getName()));
                node.addAttribute(new Attribute("margin", valueOf(set.get().getMargin())));
                node.addAttribute(new Attribute("spacing", valueOf(set.get().getSpacing())));
                node.addAttribute(new Attribute("tile-width", valueOf(set.get().getTileSize().width)));
                node.addAttribute(new Attribute("tile-height", valueOf(set.get().getTileSize().height)));
                sheets.appendChild(node);
            }
            root.appendChild(sheets);

            Element clips = new Element("audio");
            for (Resource<Audio> audio : getClips()) {
                Element node = new Element("clip");
                node.addAttribute(new Attribute("name", audio.getName()));
                clips.appendChild(node);
            }
            root.appendChild(clips);

            Element models = new Element("models");
            for (Resource<WavefrontObject> model : getModels()) {
                Element node = new Element("model");
                node.addAttribute(new Attribute("name", model.getName()));
                models.appendChild(node);
            }
            root.appendChild(models);

            Serializer serializer = new Serializer(new FileOutputStream(resourceFile), "UTF-8");
            serializer.setIndent(4);
            serializer.write(doc);
            serializer.flush();
        } catch (Exception ex) {
            ErrorHandler.alert(ex);
        }
    }
}
