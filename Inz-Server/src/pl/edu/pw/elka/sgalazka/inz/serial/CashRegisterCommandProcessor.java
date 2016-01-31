package pl.edu.pw.elka.sgalazka.inz.serial;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;
import pl.edu.pw.elka.sgalazka.inz.view.DataReceiver;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2015-11-15.
 */
public class CashRegisterCommandProcessor {

    public final static String UPDATED_DATA_FILENAME = "updatedData.txt";
    public final static String WAIT = "wait";
    public final static String NOTIFY = "notify";
    public final static String NOTIFY_PARSED = "notifyParsed";
    public final static String NO_DLL_ERROR = "noDllError";
    public final static String NO_FILE_ERROR = "noFileError";

    public final static char CR = 0x0D;
    public final static char LF = 0x0A;
    private static BlockingQueue<String> toView;

    private static boolean saveDatabase(String portName) {
        toView.add(WAIT);

        modifyConfigFile(portName);
        Log.d("Zmodyfikowano plik KONFIG");

        /*deleteDatabase();
        Log.d("Usunięto starą bazę");*/

        createDatabaseFile();
        Log.d("Zmodyfikowano plik wejsciowy");


        try {
            DLLCommands.saveWareDatabase();
        } catch (UnsatisfiedLinkError e) {
            toView.add(NOTIFY);
            toView.add(NO_DLL_ERROR);
            return false;
        }

        toView.add(NOTIFY);
        return true;
    }

    public static void getParsedData(DataReceiver callback, String portName) {
        toView.add(WAIT);

        modifyConfigFile(portName);
        modifyInputFile();

        try {
            DLLCommands.readWareDatabase();
        } catch (UnsatisfiedLinkError e) {
            toView.add(NOTIFY_PARSED);
            toView.add(NO_DLL_ERROR);
            callback.onDataReceived(null);
        }

        FileReader input = null;
        try {
            input = new FileReader(DLLCommands.OUTPUT_FILE_NAME);
        } catch (FileNotFoundException e) {
            Log.e("Brak pliku z pozycjami!");
            callback.onDataReceived(null);
            toView.add(NOTIFY_PARSED);
            return;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(DLLCommands.OUTPUT_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader bufRead = null;
        try {
            bufRead = new BufferedReader(new InputStreamReader(fileInputStream, "Cp852"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myLine = null;

        List<List<String>> list = new LinkedList<List<String>>();

        try {
            while ((myLine = bufRead.readLine()) != null) {
                if (myLine.charAt(0) == '$' && myLine.length() > 50) {
                    //System.out.println(myLine);
                    String tab[] = myLine.split("\t");
                    List<String> inner = new LinkedList<String>();
                    char vat = (char)((int)tab[2].charAt(0)+17);

                    inner.add(tab[1]);
                    inner.add(tab[7]);
                    inner.add(vat+"");
                    inner.add(tab[8]);
                    inner.add(tab[9].equals("0") ? "" : "Tak");
                    list.add(inner);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onDataReceived(list);
        toView.add(NOTIFY_PARSED);
    }

    private static void modifyConfigFile(String portname) {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLCommands.CONFIG_FILE_NAME);
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

    private static void modifyInputFile() {
        File updatedDataFile = null;
        PrintWriter writer = null;
        try {
            updatedDataFile = new File(DLLCommands.INPUT_FILE_NAME);
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

    private static boolean createDatabaseFile() {
        OutputStreamWriter writer = null;
        File updatedDataFile = null;
        OutputStream outputStream = null;
        try {
            updatedDataFile = new File(UPDATED_DATA_FILENAME);
            if (!updatedDataFile.exists()) {
                if(!updatedDataFile.createNewFile())
                    return false;
            }
            outputStream = new FileOutputStream(UPDATED_DATA_FILENAME);
            writer = new OutputStreamWriter(outputStream, "Cp852");
            //writer = new PrintWriter(updatedDataFile, "Cp852");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        Log.d("Utworzono nowy plik z danymi");
        List<Product> list = DatabaseManager.getInstance().getAllProducts();

        StringBuilder t = new StringBuilder("#01\tCOM3:9600:MUX0:1\t3\t2016.01.03\t21:30\tTowarMax\tWIN 10.00.2013\tbaza_in.txt\tbaza_out.txt");
        t.append(CR).append(LF);
        try {
            writer.write(t.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        t = new StringBuilder("#");
        t.append(CR).append(LF);
        try {
            writer.write(t.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        t = new StringBuilder("#Alfa 4095 PLU II gen. ver. 03 (identyfikator odczytany z urzadzenia i pliku ECRTBUF.TXT)");
        t.append(CR).append(LF);
        try {
            writer.write(t.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            String number = "00000" + product.getId();
            String name = product.getName().toUpperCase();
            for (int j = 0; j < 19; j++) {
                name += " ";
            }

            int vatGroup = (int) (product.getVat().toUpperCase().charAt(0)) - 64;

            StringBuilder line = new StringBuilder();
            line.append("$");
            line.append(number.substring(number.length() - 5));
            //line.append(product.getId());
            line.append('\t');
            //line.append(new String(name.substring(0,19).getBytes(), Charset.forName("Cp852")));
            line.append(name.substring(0,19));
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

            try {
                writer.write(line.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void runSaveDatabase(String filename) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                saveDatabase(filename);
            }
        };
        Thread thread = new Thread(callback);
        thread.start();
    }

    public static void runGetParsedData(DataReceiver dataReceiver, String portname) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                getParsedData(dataReceiver, portname);
            }
        };
        Thread thread = new Thread(callback);
        thread.start();
    }

    public static void setToViewQueue(BlockingQueue blockingQueue) {
        toView = blockingQueue;
    }

    private static void deleteDatabase(){
        modifyInputFile();

        DLLCommands.deleteWareDatabase();
    }
}
