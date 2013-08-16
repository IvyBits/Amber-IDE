package amber.data;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 *
 * A class to augment java.lang.System.
 *
 * @author Tudor
 */
public class OS {

    public static String[] argv() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
    }

    public static void println(Object... args) {
        for (Object arg : args) {
            print(arg);
        }
        print("\n");
    }

    public static void print(Object... args) {
        for (Object arg : args) {
            System.out.print(arg);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void printTrace() {
        new Throwable().printStackTrace(System.out);
    }

    public static void newInstance(Class main, String args) throws IOException, InterruptedException {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder =
                new ProcessBuilder(path, "-cp",
                classpath,
                main.getName(), args);
        Process process = processBuilder.start();
        process.waitFor();
    }

    public static Object[] deepcopy(Object[] arr) {
        Object[] cdata = new Object[arr.length];
        for (int i = 0; i != cdata.length; i++) {
            Object o = arr[i];
            if (o instanceof Object[]) {
                cdata[i] = deepcopy((Object[]) o);
            } else if (o instanceof Cloneable) {
                try {
                    cdata[i] = o.getClass().getDeclaredMethod("clone", null).invoke(o, null);
                    continue;
                } catch (Exception ex) {
                }
            }
            cdata[i] = o; // Shallow
        }
        return cdata;
    }
}
