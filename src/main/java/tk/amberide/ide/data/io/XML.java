package tk.amberide.ide.data.io;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Tudor
 */
public class XML {

    public static String format(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = db.parse(is);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);

            serializer.serialize(document);

            return out.toString();
        } catch (Exception e) {
            return xml;
        }
    }

    public static String toXML(Object obj) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        XMLEncoder serializer = new XMLEncoder(buffer, "UTF-8", false, 3);
        // , "UTF-8", false, 3
        serializer.writeObject(obj);
        serializer.close();
        return buffer.toString();
    }

    public static Object fromXML(String xml) {
        ByteArrayInputStream buffer = new ByteArrayInputStream(xml.getBytes());
        XMLDecoder xmlDecoder = new XMLDecoder(buffer);
        return xmlDecoder.readObject();
    }
}
