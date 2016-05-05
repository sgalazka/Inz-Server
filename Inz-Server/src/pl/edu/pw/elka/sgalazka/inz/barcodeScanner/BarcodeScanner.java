package pl.edu.pw.elka.sgalazka.inz.barcodeScanner;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import pl.edu.pw.elka.sgalazka.inz.bluetooth.BluetoothClient;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;
import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.view.ProductsPanel;

import java.util.concurrent.BlockingQueue;

/**
 * Created by ga��zka on 2015-10-17.
 */
public class BarcodeScanner implements Runnable {

    private BlockingQueue<String> fromBluetooth;
    private BlockingQueue<String> toView;
    private SerialPort serialPort = null;
    public final static String INITIALIZE = "initialize";
    public final static String STOP_RUNNING = "stop_running";
    private final char CR = 0x0D;
    private final char LF = 0x0A;

    public static String[] getSerialPortNames() {
        return SerialPortList.getPortNames();
    }

    public BarcodeScanner(BlockingQueue<String> queue, BlockingQueue<String> toView) {
        fromBluetooth = queue;
        this.toView = toView;

        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = fromBluetooth.take();
                if (!handleMessage(message))
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Scanner stops running");
    }

    private boolean handleMessage(String message) {
        String args[] = message.split(":");

        if (args[0].equals(INITIALIZE)) {
            initialize(args[1]);
        } else if (args[0].equals("B")) {
            barcodeToSell(args);
        } else if (args[0].equals(STOP_RUNNING)) {
            return false;
        }
        return true;
    }

    private boolean barcodeToSell(String args[]) {

        int newQuantity = 0;
        Product product = DatabaseManager.getInstance().findByBarcode(args[1]);
        if (product == null) {
            Log.e("Barcode: " + args[1] + " not found!");
            BluetoothClient.addToSendQueue("ENF:" + args[1]);
            return false;
        }
        int quantity = product.getQuantity();
        int sold;
        try {
            sold = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println(e);
            Log.e("SCANNER: Bad quantity format");
            return false;
        }

        newQuantity = quantity - sold;
        StringBuilder toSend = new StringBuilder("29");
        String barcode = "00000" + product.getCode();
        String tmpQuantity = "00" + (sold * 1000) + "";
        toSend.append(barcode.substring(barcode.length() - 5));
        toSend.append(tmpQuantity.substring(tmpQuantity.length() - 5));
        Log.i("Skaner sprawdza cyfrę kontrolną dla: \n" + toSend.toString());
        int checkDigit = EAN13CheckDigit.calculate(toSend.toString());
        toSend.append(checkDigit + "");

        Log.i("Skaner wysyła na kasę: " + toSend.toString());
        if (newQuantity <= 0) {
            BluetoothClient.addToSendQueue("EZQ:" + args[1]);
        } else {
            BluetoothClient.addToSendQueue("BSS");
        }

        String serialData = toSend.toString() + CR + LF;
        Log.i("SCANNER: name: " + product.getName());
        Log.i("SCANNER: barcode: " + barcode + " ,quantity: " + args[2]);
        try {
            serialPort.writeBytes(serialData.getBytes());
        } catch (SerialPortException | NullPointerException ex) {
            System.out.println(ex);
            Log.e("SCANNER: SerialPortException on send");
            return false;
        }
        product.setQuantity(newQuantity);
        toView.add(ProductsPanel.DATA_CHANGED);
        return true;
    }

    /*private void barcodeToAdd(String args[]) {
        Product product = new Product();
        product.setName(args[1]);
        product.setBarcode(args[2]);
        //product.setCode(Integer.parseInt(args[3]));
        product.setQuantity(Integer.parseInt(args[4]));
        if (DatabaseManager.getInstance().add(product)) {
            toView.add(ProductsPanel.DATA_CHANGED);
            BluetoothClient.addToSendQueue("A:" + args[1]);
            Log.i("Dodano do bazy: " + args[1] + ", ilosc: " + Integer.parseInt(args[4]));
        } else {
            BluetoothClient.addToSendQueue("DA:" + args[1]);
        }
    }*/

    private void initialize(String portName) {

        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_EVEN);
        } catch (SerialPortException ex) {
            System.out.println(ex);
            Log.e("SCANNER: SerialPortException on open");
            return;
        }
        Log.i("Scanner opened port: " + portName);
    }
}
