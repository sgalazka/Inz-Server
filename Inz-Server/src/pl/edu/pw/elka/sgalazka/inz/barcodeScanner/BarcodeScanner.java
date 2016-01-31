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
                //if (serialPort != null && serialPort.isOpened())
                handleMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

    private void handleMessage(String message) {
        String args[] = message.split(":");

        if (args[0].equals(INITIALIZE)) {
            initialize(args[1]);
            return;
        } else if (args[0].equals("B")) {
            barcodeToSell(args);
        } /*else if (args[0].equals("D")) {
            barcodeToAdd(args);
        }*/
    }

    private boolean barcodeToSell(String args[]) {

        int newQuantity = 0;
        Product product = DatabaseManager.getInstance().findByBarcode(args[1]);
        if (product == null) {
            Log.e("Barcode: " + args[1] + " not found!");
            BluetoothClient.addToSendQueue("N:" + args[1]);
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

        if (sold > quantity) {
            Log.e("Produktu " + args[2] + " o kodzie: " + args[1] + "\n, nie ma w magazynie");
            BluetoothClient.addToSendQueue("N:" + args[1]);
            newQuantity = 0;
        }
        else
            newQuantity = quantity - sold;
        StringBuilder toSend = new StringBuilder("29");
        String barcode = "00000" + product.getId();
        String tmpQuantity = "00" + (sold * 1000) + "";
        toSend.append(barcode.substring(barcode.length() - 5));
        toSend.append(tmpQuantity.substring(tmpQuantity.length() - 5));
        int checkDigit = EAN13CheckDigit.calculate(toSend.toString());
        toSend.append(checkDigit+"");

        Log.d("Skaner wysyła na kasę: "+toSend.toString());

        String serialData = toSend.toString() + CR + LF;
        Log.d("SCANNER: name: " + product.getName());
        Log.d("SCANNER: barcode: " + barcode + " ,quantity: " + args[2]);
        try {
            serialPort.writeBytes(serialData.getBytes());
        } catch (SerialPortException ex) {
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
            Log.d("Dodano do bazy: " + args[1] + ", ilosc: " + Integer.parseInt(args[4]));
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
        Log.d("Scanner opened port: " + portName);
    }
}
