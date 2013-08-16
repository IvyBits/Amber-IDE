package amber.gl;

import java.io.*;
import java.net.URL;
import static org.lwjgl.LWJGLUtil.*;
import static org.lwjgl.Sys.*;

/**
 *
 * @author Tudor
 */
public final class Natives {

    private static final String LWJGL_DIR = System.getProperty("java.io.tmpdir") + File.separatorChar + "ambergl290";

    public static void unpack() {
        // Make LWJGL load binaries from %temp%
        System.setProperty("org.lwjgl.librarypath", LWJGL_DIR);
        String osArch = System.getProperty("os.arch");

        boolean is64Bit = "amd64".equals(osArch) || "x86_64".equals(osArch);
        switch (getPlatform()) {
            case PLATFORM_WINDOWS:
                if (is64Bit) {
                    unpack("windows/OpenAL64.dll", "OpenAL64.dll");
                    unpack("windows/lwjgl64.dll", "lwjgl64.dll");
                } else {
                    unpack("windows/OpenAL32.dll", "OpenAL32.dll");
                    unpack("windows/lwjgl.dll", "lwjgl.dll");
                }
                break;
            case PLATFORM_LINUX:
                if (is64Bit) {
                    unpack("linux/libopenal64.so", "libopenal64.so");
                    unpack("linux/liblwjgl64.so", "liblwjgl64.so");
                } else {
                    unpack("linux/libopenal.so", "libopenal.so");
                    unpack("linux/liblwjgl.so", "liblwjgl.so");
                }
                break;
            case PLATFORM_MACOSX:
                unpack("macosx/liblwjgl.jnilib", "liblwjgl.jnilib");
                unpack("macosx/libopenal.dylib", "libopenal.dylib");
                break;
            default:
                throw new UnsupportedOperationException("unsupported platform");

        }
    }

    private static void unpack(String path, String name) {
        try {
            URL url = ClassLoader.getSystemResource("native/" + path);
            File pathDir = new File(LWJGL_DIR);
            pathDir.mkdirs();
            File libfile = new File(pathDir, name);

            if (!libfile.exists()) { // This causes UnsatisfiedLinkErrors when updating from older LWJGL binaries
                libfile.deleteOnExit();
                InputStream in = url.openStream();
                OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));

                int len;
                byte[] buffer = new byte[8192];
                while ((len = in.read(buffer)) > -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();
            }
        } catch (IOException x) {
            throw new RuntimeException("could not unpack binaries", x);
        }
    }
}
