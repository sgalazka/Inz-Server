package pl.edu.pw.elka.sgalazka.inz.serial;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Created by ga��zka on 2015-11-01.
 */
public class DLLCommandProcessor {

    public final static String databaseFileName = "towarmaxJ.txt";

    public interface simpleDLL extends Library {
        simpleDLL INSTANCE = (simpleDLL) Native.loadLibrary("WinIP", simpleDLL.class);

        char __OPSprzed(String par1, String par2);

        char __TowarMax(String par1, String par2);
    }

    public static void readWareDatabase() {
        simpleDLL sdll = simpleDLL.INSTANCE;
        char err = sdll.__OPSprzed("baza_in.txt", databaseFileName);
        System.out.println(err);
    }

    public static void readReceipts() {
        simpleDLL sdll = simpleDLL.INSTANCE;
        char err = sdll.__TowarMax("baza_in.txt", "towarmaxJ.txt");
        System.out.println(err);
    }
}
