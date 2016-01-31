package pl.edu.pw.elka.sgalazka.inz;

import pl.edu.pw.elka.sgalazka.inz.bluetooth.BluetoothServer;
import pl.edu.pw.elka.sgalazka.inz.serial.CashRegisterCommandProcessor;
import pl.edu.pw.elka.sgalazka.inz.barcodeScanner.BarcodeScanner;
import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.Log.LogType;
import pl.edu.pw.elka.sgalazka.inz.Log.viewLog.LogPanel;
import pl.edu.pw.elka.sgalazka.inz.view.MainWindow;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ga��zka on 2015-10-07.
 */
public class InzMain {

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {

        BlockingQueue<LogType> toLogger = new LinkedBlockingQueue<>();
        BlockingQueue<String> toScanner = new LinkedBlockingQueue<>();
        BlockingQueue<String> toView = new LinkedBlockingQueue<>();
        final LogPanel logPanel = new LogPanel(toLogger);
        Log.setQueue(toLogger);
        final MainWindow mainWindow = new MainWindow(logPanel, toView, toScanner);
        CashRegisterCommandProcessor.setToViewQueue(toView);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Thread view = new Thread(mainWindow);
                view.start();
            }
        });

        final BluetoothServer bluetoothServer = new BluetoothServer(toScanner, toView);
        Thread bluetoothThread = new Thread(bluetoothServer);
        bluetoothThread.start();

        final BarcodeScanner barcodeScanner = new BarcodeScanner(toScanner, toView);
        Thread scannerThread = new Thread(barcodeScanner);
        scannerThread.start();

        Thread loggerThread = new Thread(logPanel);
        loggerThread.start();
    }
}
