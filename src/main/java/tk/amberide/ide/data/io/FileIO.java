package tk.amberide.ide.data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Tudor
 */
public class FileIO {

    public static void copy(File from, File to) throws IOException {
        write(new FileInputStream(from), to);
    }

    public static void move(File from, File to) throws IOException {
        copy(from, to);
        if (to.exists() && from.canWrite()) {
            from.delete();
        }
    }

    public static void write(InputStream in, File to) throws IOException {
        if (!to.exists()) {
            to.createNewFile();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(to);
            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void rename(File file, String name) throws IOException {
        file.renameTo(new File(file.getParentFile(), name));
    }

    public static String read(File file) throws IOException {
        return read(new FileInputStream(file));
    }

    public static String read(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder read = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            read.append(line).append("\n");
        }
        br.close();
        return read.toString();
    }

    public static String[] readArray(File file) throws IOException {
        return read(file).split("\n");
    }

    public static String[] readArray(InputStream in) throws IOException {
        return read(in).split("\n");
    }

    public static String getFileExtension(File file) {
        String name = file.getAbsolutePath();
        String extension = "";

        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));

        if (i > p) {
            extension = name.substring(i + 1);
        }
        return extension;
    }

    public static File[] getFilesByName(String name, File folder) throws IOException {
        ArrayList<File> files = new ArrayList<File>();
        if (folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                if (getFileName(f).equals(name)) {
                    files.add(f);
                }
            }
            return files.toArray(new File[0]);
        } else {
            throw new IOException("argument file is not a folder");
        }
    }

    public static String getFileName(File file) {
        String fname = file.getName();
        int dot = fname.lastIndexOf(".");
        if (dot > -1) {
            fname = fname.substring(0, dot);
        }
        return fname;
    }
}
