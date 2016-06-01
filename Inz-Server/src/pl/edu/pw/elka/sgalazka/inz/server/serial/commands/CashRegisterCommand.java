package pl.edu.pw.elka.sgalazka.inz.server.serial.commands;

import com.sun.istack.internal.NotNull;
import pl.edu.pw.elka.sgalazka.inz.server.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.server.serial.DLLFunctions;
import pl.edu.pw.elka.sgalazka.inz.server.view.DataReceiver;

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
    public final static String FILE_ENCODING = "Cp852";
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

    protected static void modifyConfigFile(String portName) {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLFunctions.CONFIG_FILE_NAME);
            if (!updatedDataFile.exists()) {
                updatedDataFile.createNewFile();
            }
            writer = new PrintWriter(updatedDataFile, FILE_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        writer.print("$01\t" + portName + ":9600:MUX0:1\t3");
        writer.close();
    }

    @SuppressWarnings("all")
    protected static void modifyInputFile() {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLFunctions.INPUT_FILE_NAME);
            if (!updatedDataFile.exists()) {
                updatedDataFile.createNewFile();
            }
            writer = new PrintWriter(updatedDataFile, FILE_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        writer.print("#1" + CR + LF + "#" + CR + LF + "#");
        writer.close();
    }

    //@SuppressWarnings("unused")
    protected ReadFileDescriptor openFileToRead(@NotNull String name) {
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
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, FILE_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Zły format pliku: " + name);
            return null;
        }
        return new ReadFileDescriptor(fileInputStream, bufferedReader);
    }

    protected static WriteFileDescriptor openFileToWrite(@NotNull String fileName) {
        File updatedDataFile;
        OutputStream outputStream;
        OutputStreamWriter writer;
        try {
            updatedDataFile = new File(fileName);
            if (!updatedDataFile.exists()) {
                if (!updatedDataFile.createNewFile())
                    return null;
            }
            outputStream = new FileOutputStream(fileName);
            writer = new OutputStreamWriter(outputStream, FILE_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new WriteFileDescriptor(outputStream, writer);
    }

    protected static class WriteFileDescriptor {
        public OutputStreamWriter writer;
        public OutputStream stream;

        public WriteFileDescriptor(@NotNull OutputStream stream, @NotNull OutputStreamWriter writer) {
            this.stream = stream;
            this.writer = writer;
        }
    }

    protected class ReadFileDescriptor {
        public FileInputStream fileInputStream;
        public BufferedReader bufferedReader;

        public ReadFileDescriptor(@NotNull FileInputStream fileInputStream, @NotNull BufferedReader bufferedReader) {
            this.fileInputStream = fileInputStream;
            this.bufferedReader = bufferedReader;
        }
    }
}
