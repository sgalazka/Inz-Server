package pl.edu.pw.elka.sgalazka.inz;

import pl.edu.pw.elka.sgalazka.inz.bluetooth.BluetoothServer;
import pl.edu.pw.elka.sgalazka.inz.serial.BarcodeScanner;
import pl.edu.pw.elka.sgalazka.inz.view.Log;
import pl.edu.pw.elka.sgalazka.inz.view.LogType;
import pl.edu.pw.elka.sgalazka.inz.view.MainPanel;
import pl.edu.pw.elka.sgalazka.inz.view.MainWindow;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ga³¹zka on 2015-10-07.
 */
public class InzMain {

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {

        BlockingQueue<LogType> blockingQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> toScanner = new LinkedBlockingQueue<>();
        Log.setQueue(blockingQueue);
        final MainWindow mainWindow = new MainWindow(blockingQueue);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Thread view = null;
                view = new Thread(mainWindow);
                view.start();
            }
        });
        final BarcodeScanner barcodeScanner = new BarcodeScanner(toScanner);
        final BluetoothServer bluetoothServer = new BluetoothServer(blockingQueue, toScanner);
        Thread bluetoothThread = new Thread(bluetoothServer);
        Thread scannerThread = new Thread(barcodeScanner);
        scannerThread.start();
        bluetoothThread.start();

    }
}
