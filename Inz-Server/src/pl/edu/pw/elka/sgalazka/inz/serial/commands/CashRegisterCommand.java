package pl.edu.pw.elka.sgalazka.inz.serial.commands;

import com.sun.istack.internal.NotNull;
import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.serial.DLLFunctions;
import pl.edu.pw.elka.sgalazka.inz.view.DataReceiver;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2016-02-11.
 */
public abstract class CashRegisterCommand implements Runnable {


    public final static String WAIT = "wait";
    public final static String NOTIFY = "notify";
    public final static String NO_DLL_ERROR = "noDllError";
    public final static String NO_FILE_ERROR = "noFileError";
    protected final static char CR = 0x0D;
    protected final static char LF = 0x0A;
    protected static BlockingQueue<String> toView;
    protected String portName;
    protected DataReceiver dataReceiver;

    public CashRegisterCommand(BlockingQueue<String> queue, String portName) {
        toView = queue;
        this.portName = portName;
    }

    public CashRegisterCommand(BlockingQueue<String> queue, String portName, DataReceiver receiver) {
        toView = queue;
        this.portName = portName;
        this.dataReceiver = receiver;
    }

    abstract void process();

    @Override
    public void run() {
        process();
    }

    protected static void modifyConfigFile(String portname) {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLFunctions.CONFIG_FILE_NAME);
            if (!updatedDataFile.exists()) {
                updatedDataFile.createNewFile();
            }
            writer = new PrintWriter(updatedDataFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        writer.print("$01\t" + portname + ":9600:MUX0:1\t3");
        writer.close();
    }

    protected static void modifyInputFile() {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLFunctions.INPUT_FILE_NAME);
            if (!updatedDataFile.exists()) {
                updatedDataFile.createNewFile();
            }
            writer = new PrintWriter(updatedDataFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        writer.print("#1" + CR + LF + "#" + CR + LF + "#");
        writer.close();
    }

    protected FileDescriptor openFileToRead(@NotNull String name) {
        FileReader fileReader;
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        try {
            fileReader = new FileReader(name);
        } catch (FileNotFoundException e) {
            Log.e("Brak pliku: " + name);
            return null;
        }
        try {
            fileInputStream = new FileInputStream(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Brak pliku: " + name);
            return null;
        }
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "Cp852"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Zły format pliku: " + name);
            return null;
        }
        return new FileDescriptor(fileInputStream, bufferedReader);
    }

    protected class FileDescriptor {
        public FileInputStream fileInputStream;
        public BufferedReader bufferedReader;

        public FileDescriptor(FileInputStream fileInputStream, BufferedReader bufferedReader) {
            this.fileInputStream = fileInputStream;
            this.bufferedReader = bufferedReader;
        }
    }
}
