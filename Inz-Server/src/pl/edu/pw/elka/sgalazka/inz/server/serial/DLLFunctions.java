package pl.edu.pw.elka.sgalazka.inz.server.serial;

import com.sun.jna.Library;
import com.sun.jna.Native;
import pl.edu.pw.elka.sgalazka.inz.server.serial.commands.SaveDatabaseCommand;

/**
 * Created by ga��zka on 2015-11-01.
 */
public class DLLFunctions {

    public final static String OUTPUT_FILE_NAME = "out.txt";
    public final static String CONFIG_FILE_NAME = "KONFIG.txt";
    public final static String INPUT_FILE_NAME = "in.txt";

    private final static Object lock = new Object();

    public interface simpleDLL extends Library {

        simpleDLL INSTANCE = (simpleDLL) Native.loadLibrary("WinIP", simpleDLL.class);

        char __ZTowar(String par1);

        char __TowarMax(String par1, String par2);

        char __KTowMax(String par1);

        char __OPodatek(String par1, String par2);
    }

    public static int saveWareDatabase() throws UnsatisfiedLinkError {
        synchronized (lock) {
            simpleDLL sdll = simpleDLL.INSTANCE;
            char err = sdll.__ZTowar(SaveDatabaseCommand.UPDATED_DATA_FILENAME);
            System.out.println((int)err);
            return (int)err;
        }
    }

    public static int readWareDatabase() throws UnsatisfiedLinkError {
        synchronized (lock) {
            simpleDLL sdll = simpleDLL.INSTANCE;
            char err = sdll.__TowarMax(INPUT_FILE_NAME, OUTPUT_FILE_NAME);
            System.out.println((int)err);
            return (int)err;
        }
    }

    public static int deleteWareDatabase() throws UnsatisfiedLinkError {
        synchronized (lock) {
            simpleDLL sdll = simpleDLL.INSTANCE;
            char err = sdll.__KTowMax(INPUT_FILE_NAME);
            System.out.println((int)err);
            return (int)err;
        }
    }

    public static int readVatGroups() throws UnsatisfiedLinkError {
        synchronized (lock) {
            simpleDLL sdll = simpleDLL.INSTANCE;
            char err = sdll.__OPodatek(INPUT_FILE_NAME, OUTPUT_FILE_NAME);
            System.out.println((int)err);
            return (int)err;
        }
    }

}

