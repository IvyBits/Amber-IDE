package amber.data.state.xml;

import amber.data.io.XML;
import amber.data.state.AbstractStateManager;
import amber.data.state.Scope;
import amber.data.state.node.FieldState;
import amber.data.state.node.IState;
import amber.data.state.node.SimpleState;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Serializer;
import nu.xom.Text;

public class XMLStateManager extends AbstractStateManager {

    @Override
    public void emitStates(int id) throws Exception {
        Element root = new Element("storage");
        Document doc = new Document(root);

        Element fields = new Element("fields");
        List<IState> statesList = states.get(id);
        for (int i = 0; i != statesList.size(); i++) {
            IState state = statesList.get(i);
            if (state instanceof FieldState) {
                FieldState fs = (FieldState) state;
                Object data = fs.get();
                System.out.println(fs + " data is " + data + " while saving");
                if (data != null) {
                    Element field = new Element("field");
                    field.addAttribute(new Attribute("path", fs.getName()));
                    field.appendChild(new Text(XML.toXML(data)));
                    fields.appendChild(field);
                }
            } else {
                Element store = new Element("state");
                store.addAttribute(new Attribute("name", state.getName()));
                store.appendChild(new Text(XML.toXML(state.get())));
                root.appendChild(store);
            }
        }
        root.appendChild(fields);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        Serializer serializer = new Serializer(buffer, "UTF-8") {
            @Override
            protected void write(Text text) throws IOException {
                writeRaw(text.getValue());
            }
        };

        serializer.write(doc);
        serializer.flush();

        Scope scope = Scope.predefinedScope(id);
        File output = new File(resolveMacros(scope.location()) + File.separator + scope.name() + ".xml");
        output.mkdirs();
        output.delete();
        output.createNewFile();
        new FileOutputStream(output).write(XML.format(buffer.toString("UTF-8")).getBytes());
    }

    @Override
    public void loadStates(int id) throws Exception {
        Scope scope = Scope.predefinedScope(id);
        File storage = new File(resolveMacros(scope.location()) + File.separator + scope.name() + ".xml");
        try {
            Document doc = new Builder().build(storage);
            Element root = doc.getRootElement();
            Elements states = root.getChildElements();
            for (int s = 0; s != states.size(); s++) {
                Element state = states.get(s);
                if (state.getLocalName().equals("fields")) {
                    Elements fields = state.getChildElements();
                    for (int i = 0; i != fields.size(); i++) {
                        Element fieldState = fields.get(i);
                        String path = fieldState.getAttributeValue("path");
                        String[] split = path.split("/");
                        Class owner = Class.forName(split[0]);
                        if (owner != null) {
                            Field f = owner.getDeclaredField(split[1]);
                            if (f != null && Modifier.isStatic(f.getModifiers())) {
                                Object data;
                                try {
                                    data = XML.fromXML(fieldState.getChildElements("java").get(0).toXML());
                                } catch (Exception e) {
                                    System.err.println("Failed to read field state!");
                                    e.printStackTrace();
                                    continue;
                                }
                                f.setAccessible(true);
                                f.set(null, data);
                                IState loaded = new FieldState(f);
                                addState(id, loaded);
                                fireStateLoaded(loaded, id);
                            }
                        }
                    }
                } else {
                    String name = state.getAttributeValue("name");
                    Object data;
                    try {
                        data = XML.fromXML(state.getChildElements("java").get(0).toXML());
                    } catch (Exception e) {
                        System.err.println("Failed to read state " + name + "!" + state.getChildElements("java").get(0).toXML());
                        e.printStackTrace();
                        continue;
                    }
                    IState loaded = new SimpleState(name, data);
                    addState(id, loaded);
                    fireStateLoaded(loaded, id);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Umm... Storage file " + storage + " doesn't exist.");
            e.printStackTrace();
        }
    }
}
