package pl.edu.pw.elka.sgalazka.inz.Log;

import java.util.concurrent.BlockingQueue;

/**
 * Created by ga��zka on 2015-10-10.
 */
public class Log {
    private static BlockingQueue<LogType> logQueue;

    public static void setQueue(BlockingQueue queue) {
        logQueue = queue;
    }

    private static final Object lock = new Object();

    public static void d(String message) {
        synchronized (lock) {
            LogType logType = new LogType();
            logType.setMessage(message);
            logType.setType('I');
            logQueue.add(logType);
        }
    }

    public static void e(String message) {
        synchronized (lock) {
            LogType logType = new LogType();
            logType.setMessage(message);
            logType.setType('E');
            logQueue.add(logType);
        }
    }

    public static void w(String message) {
        synchronized (lock) {
            LogType logType = new LogType();
            logType.setMessage(message);
            logType.setType('W');
            logQueue.add(logType);
        }
    }

}
