package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.barcodeScanner.BarcodeScanner;
import pl.edu.pw.elka.sgalazka.inz.bluetooth.BluetoothServer;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.EntityManagerUtil;
import pl.edu.pw.elka.sgalazka.inz.serial.commands.CashRegisterCommand;
import pl.edu.pw.elka.sgalazka.inz.serial.commands.DeleteDatabaseCommand;
import pl.edu.pw.elka.sgalazka.inz.serial.commands.GetPositionsCommand;
import pl.edu.pw.elka.sgalazka.inz.serial.commands.GetVatGroupsCommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ga��zka on 2015-10-07.
 */
public class MainWindow implements Runnable {
    private JFrame mainFrame;
    private MainPanel mainPanel;
    private JPanel cards;
    private ProductsPanel productsPanel;
    private PositionsPanel positionsPanel;
    private BlockingQueue<String> queue;
    private static final String STOP = "stop";

    public MainWindow(JPanel logPanel, BlockingQueue<String> queue, BlockingQueue<String> toScanner) {
        this.queue = queue;
        Log.e("Przykładowy błąd");
        Log.w("Przykładowe ostrzeżenie");
        Log.d("Przykładowy debug");
        Log.i("Przykładowa informacja");
        //this.toScanner = toScanner;
        cards = new JPanel(new CardLayout());
        mainFrame = new JFrame("Inz-Server");
        mainFrame.setLayout(new BorderLayout());
        logPanel.setMinimumSize(new Dimension(350, 0));
        logPanel.setMaximumSize(new Dimension(500, 100));
        positionsPanel = new PositionsPanel(cards);
        mainPanel = new MainPanel(cards, toScanner, positionsPanel);
        productsPanel = new ProductsPanel(cards);
        mainFrame.setSize(new Dimension(900, 700));
        mainFrame.setMinimumSize(new Dimension(750, 300));
        cards.add(mainPanel, "mainPanel");
        cards.add(productsPanel, "databasePanel");
        cards.add(positionsPanel, "positionsPanel");

        mainFrame.add(cards, BorderLayout.CENTER);
        mainFrame.add(logPanel, BorderLayout.EAST);
        mainFrame.setSize(1200, 700);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closed");
                Log.stopRunning();
                toScanner.add(BarcodeScanner.STOP_RUNNING);
                DatabaseManager.getInstance().endTransaction();
                EntityManagerUtil.closeConnection();
                BluetoothServer.setRunning(false);
                queue.add(STOP);
                e.getWindow().dispose();
            }
        });
    }

    @Override
    public void run() {
        mainPanel.showScannerPortQuestion();
        boolean running = true;
        while (running) {
            try {
                String data = queue.take();
                String tmp[] = data.split(":");

                switch (tmp[0]) {
                    case CashRegisterCommand.WAIT:
                        mainPanel.enableWaitingState();
                        break;
                    case CashRegisterCommand.NOTIFY:
                        mainPanel.disableWaitingState();
                        break;
                    case CashRegisterCommand.NO_DLL_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Program nie może znaleźć pliku WinIP.dll!",
                                "Brak pliku",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case ProductsPanel.DATA_CHANGED:
                        productsPanel.notifyChange();
                        break;
                    case GetPositionsCommand.NOTIFY_PARSED:
                        mainPanel.disableWaitingState();
                        mainPanel.goToPositionsPanel();
                        break;
                    case GetVatGroupsCommand.NOTIFY_VAT:
                        mainPanel.disableWaitingState();
                        VatGroupsDialog vatGroupsDialog = new VatGroupsDialog(new JFrame(), data);
                        break;
                    case CashRegisterCommand.NO_FILE_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Program nie może połączyć się z kasą",
                                "Brak pliku",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case DeleteDatabaseCommand.NOTIFY_DELETE:
                        mainPanel.disableWaitingState();
                        mainPanel.showDatabaseSave();
                        break;
                    case DeleteDatabaseCommand.DELETE_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Błąd obsługi kasy fiskalnej",
                                "Błąd",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case STOP:
                        running = false;
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("MainWindow stops running");
    }


}
