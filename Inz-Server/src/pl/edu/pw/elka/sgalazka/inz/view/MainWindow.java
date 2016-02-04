package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.bluetooth.BluetoothServer;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.EntityManagerUtil;
import pl.edu.pw.elka.sgalazka.inz.serial.CashRegisterCommandProcessor;

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

    public MainWindow(JPanel logPanel, BlockingQueue<String> queue, BlockingQueue<String> toScanner) {
        this.queue = queue;
        //this.toScanner = toScanner;
        cards = new JPanel(new CardLayout());
        mainFrame = new JFrame("Inz-Server");
        mainFrame.setLayout(new BorderLayout());
        logPanel.setMinimumSize(new Dimension(350, 0));
        positionsPanel = new PositionsPanel(cards);
        mainPanel = new MainPanel(cards, toScanner, positionsPanel);
        productsPanel = new ProductsPanel(cards);
        mainFrame.setSize(new Dimension(900, 700));
        mainFrame.setMinimumSize(new Dimension(750, 300));
        mainFrame.setBackground(new Color(40));
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
                DatabaseManager.getInstance().endTransaction();
                EntityManagerUtil.closeConnection();
                BluetoothServer.setRunning(false);
                e.getWindow().dispose();
            }
        });
    }

    @Override
    public void run() {
        mainPanel.showScannerPortQuestion();
        while (true) {
            try {
                String data = queue.take();
                String tmp[] = data.split(":");

                switch (tmp[0]) {
                    case CashRegisterCommandProcessor.WAIT:
                        mainPanel.enableWaitingState();
                        break;
                    case CashRegisterCommandProcessor.NOTIFY:
                        mainPanel.disableWaitingState();
                        break;
                    case CashRegisterCommandProcessor.NO_DLL_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Program nie może znaleźć pliku WinIP.dll!",
                                "Brak pliku",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case ProductsPanel.DATA_CHANGED:
                        productsPanel.notifyChange();
                        break;
                    case CashRegisterCommandProcessor.NOTIFY_PARSED:
                        mainPanel.disableWaitingState();
                        mainPanel.goToPositionsPanel();
                        break;
                    case CashRegisterCommandProcessor.NOTIFY_VAT:
                        mainPanel.disableWaitingState();
                        VatGroupsDialog vatGroupsDialog = new VatGroupsDialog(new JFrame(), data);
                        break;
                    case CashRegisterCommandProcessor.NO_FILE_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Program nie może połączyć się z kasą",
                                "Brak pliku",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case CashRegisterCommandProcessor.NOTIFY_DELETE:
                        mainPanel.disableWaitingState();
                        mainPanel.showDatabaseSave();
                        break;
                    case CashRegisterCommandProcessor.DELETE_ERROR:
                        mainPanel.disableWaitingState();
                        JOptionPane.showMessageDialog(mainFrame,
                                "Błąd obsługi kasy fiskalnej",
                                "Błąd",
                                JOptionPane.ERROR_MESSAGE);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
