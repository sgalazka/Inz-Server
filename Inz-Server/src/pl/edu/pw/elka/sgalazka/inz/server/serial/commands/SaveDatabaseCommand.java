package pl.edu.pw.elka.sgalazka.inz.server.serial.commands;

import pl.edu.pw.elka.sgalazka.inz.server.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.server.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.server.database.Product;

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
        Log.i("Zmodyfikowano plik KONFIG");
        //modifyInputFile();
        DatabaseManager.getInstance().packUpId();
        createDatabaseFile();
        Log.i("Zmodyfikowano plik wejsciowy");
/*
        try {
            DLLFunctions.saveWareDatabase();
        } catch (UnsatisfiedLinkError e) {
            toView.add(CashRegisterCommand.NOTIFY);
            toView.add(CashRegisterCommand.NO_DLL_ERROR);
            return;
        }

        Log.i("Zapisano bazę danych");*/
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        toView.add(CashRegisterCommand.NOTIFY);
    }

    private static boolean createDatabaseFile() {
        WriteFileDescriptor fd = openFileToWrite(UPDATED_DATA_FILENAME);
        if (fd == null)
            return false;

        Log.i("Utworzono nowy plik z danymi");
        List<Product> list = DatabaseManager.getInstance().getAllProducts();
        try {
            writeHeader(fd.writer);
            for (Product product : list) {
                String number = "00000" + product.getCode();
                String name = product.getName().toUpperCase();
                for (int j = 0; j < 19; j++) {
                    name += " ";
                }
                int vatGroup = (int) (product.getVat().toUpperCase().charAt(0)) - 64;
                fd.writer.write("$" + number.substring(number.length() - 5) + '\t' + name.substring(0, 19) + '\t'
                        + vatGroup + "\t1\t3\t1\t0\t00000029" + number.substring(number.length() - 5) + '\t'
                        + product.getPrice() + '\t' + (product.getPackaging() == 0 ? 0 : 1) + CR + LF);
            }
            fd.writer.close();
            fd.stream.close();
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

}
