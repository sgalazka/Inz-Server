package pl.edu.pw.elka.sgalazka.inz.serial.commands;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;
import pl.edu.pw.elka.sgalazka.inz.serial.DLLFunctions;

import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2016-02-12.
 */
public class SaveDatabaseCommand extends CashRegisterCommand {

    public final static String UPDATED_DATA_FILENAME = "updatedData.txt";

    public SaveDatabaseCommand(BlockingQueue<String> queue, String portName) {
        super(queue, portName);
    }

    @Override
    void process() {
        toView.add(CashRegisterCommand.WAIT);

        modifyConfigFile(portName);
        Log.d("Zmodyfikowano plik KONFIG");
        modifyInputFile();

        createDatabaseFile();
        Log.d("Zmodyfikowano plik wejsciowy");

        try {
            DLLFunctions.saveWareDatabase();
        } catch (UnsatisfiedLinkError e) {
            toView.add(CashRegisterCommand.NOTIFY);
            toView.add(CashRegisterCommand.NO_DLL_ERROR);
            return;
        }

        Log.d("Zapisano bazę danych");

        toView.add(CashRegisterCommand.NOTIFY);
    }

    private static boolean createDatabaseFile() {
        WriteFileDescriptor fd = openFileToWrite(UPDATED_DATA_FILENAME);

        Log.d("Utworzono nowy plik z danymi");
        List<Product> list = DatabaseManager.getInstance().getAllProducts();
        try {
            writeHeader(fd.writer);
            for (Product product : list) {
                String number = "00000" + product.getId();
                String name = product.getName().toUpperCase();
                for (int j = 0; j < 19; j++) {
                    name += " ";
                }
                int vatGroup = (int) (product.getVat().toUpperCase().charAt(0)) - 64;
                StringBuilder line = new StringBuilder();
                line.append("$");
                line.append(number.substring(number.length() - 5));
                line.append('\t');
                line.append(name.substring(0, 19));
                line.append('\t');
                line.append(vatGroup);
                line.append("\t1\t3\t1\t0\t00000029");
                line.append(number.substring(number.length() - 5));
                line.append('\t');
                line.append(product.getPrice());
                line.append('\t');
                line.append(product.getPackaging() == 0 ? 0 : 1);
                line.append(CR);
                line.append(LF);
                fd.writer.write(line.toString());
            }
            fd.stream.close();
            fd.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void writeHeader(OutputStreamWriter writer) throws IOException {
        StringBuilder t = new StringBuilder("#01\tCOM3:9600:MUX0:1\t3\t2016.01.03\t21:30\tTowarMax\tWIN 10.00.2013\tbaza_in.txt\tbaza_out.txt");
        t.append(CR).append(LF);

        t.append("#");
        t.append(CR).append(LF);

        t.append("#Alfa 4095 PLU II gen. ver. 03 (identyfikator odczytany z urzadzenia i pliku ECRTBUF.TXT)");
        t.append(CR).append(LF);

        writer.write(t.toString());
    }

    private static WriteFileDescriptor openFileToWrite(String fileName) {
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
            writer = new OutputStreamWriter(outputStream, "Cp852");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new WriteFileDescriptor(outputStream, writer);
    }

    private class WriteFileDescriptor {
        public OutputStreamWriter writer;
        public OutputStream stream;

        public WriteFileDescriptor(OutputStream stream, OutputStreamWriter writer) {
            this.stream = stream;
            this.writer = writer;
        }
    }
}
