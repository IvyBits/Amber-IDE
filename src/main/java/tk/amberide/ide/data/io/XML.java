package tk.amberide.ide.data.io;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import java.io.ByteArrayInputStream;

/**
 *
 * @author Tudor
 */
public class XML {

    public static String toXML(Object obj) {
        XStream xtr = new XStream(new XomDriver());
        return xtr.toXML(obj);
    }

    public static Object fromXML(String xml) {
        return new XStream(new XomDriver()).fromXML(new ByteArrayInputStream(xml.getBytes()));
    }
}
