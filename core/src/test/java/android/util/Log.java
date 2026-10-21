package android.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/** JVM-only logging adapter; Android's stub logger cannot run in plain unit tests. */
public final class Log {
    private Log() { }

    public static int d(String tag, String message) { return 0; }
    public static int i(String tag, String message) { return 0; }

    public static int e(String tag, String message) {
        System.err.println(tag + ": " + message);
        return 0;
    }

    public static int e(String tag, String message, Throwable error) {
        e(tag, message);
        error.printStackTrace();
        return 0;
    }

    public static String getStackTraceString(Throwable error) {
        StringWriter text = new StringWriter();
        error.printStackTrace(new PrintWriter(text));
        return text.toString();
    }
}
