package tk.amberide.ide.data.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Tudor
 */
public class LoggedOutputStream extends PrintStream {

    public LoggedOutputStream(File to) throws IOException {
        super(new FileOutputStream(to));
    }
}
