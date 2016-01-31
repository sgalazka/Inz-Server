package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.serial.CashRegisterCommandProcessor;
import pl.edu.pw.elka.sgalazka.inz.barcodeScanner.BarcodeScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ga��zka on 2015-10-07.
 */
public class MainPanel extends JPanel {

    private JButton parseDataButton;
    private JButton showDataButton;
    private JButton databaseButton;
    private JButton scannerPortButton;
    private JLabel waitLabel;
    private JProgressBar progressBar;
    private BlockingQueue<String> toScanner;
    private JPanel cardLayout;

    MainPanel(JPanel cardLayout, BlockingQueue<String> toScanner, PositionsPanel positionsPanel) {
        this.toScanner = toScanner;
        this.cardLayout = cardLayout;
        JPanel inner = new JPanel(new GridLayout(6, 1));
        JPanel waitPanel = new JPanel(new GridLayout(2, 1));
        JPanel waitGrid = new JPanel(new BorderLayout());
        parseDataButton = new JButton("Zapisz baze do kasy");
        showDataButton = new JButton("Pokaz pozycje na kasie");
        databaseButton = new JButton("Zarzadzaj baza towarow");
        scannerPortButton = new JButton("Ustaw port skanera");
        waitLabel = new JLabel(" ");
        progressBar = new JProgressBar();

        parseDataButton.setSize(150, 35);
        showDataButton.setSize(150, 35);
        databaseButton.setSize(150, 35);
        scannerPortButton.setSize(150, 35);
        waitLabel.setSize(150, 35);
        progressBar.setSize(150, 35);

        JPanel grid = new JPanel(new BorderLayout());
        inner.add(parseDataButton);
        inner.add(showDataButton);
        inner.add(databaseButton);
        inner.add(scannerPortButton);
        inner.add(waitLabel);
        inner.add(progressBar);
        grid.add(inner, BorderLayout.CENTER);

       /* progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        waitGrid.add(waitLabel);
        waitGrid.add(progressBar);
        waitPanel.add(waitGrid, BorderLayout.SOUTH);*/
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        add(grid, BorderLayout.CENTER);
        //add(waitPanel, BorderLayout.SOUTH);

        databaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });

        showDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ports[] = BarcodeScanner.getSerialPortNames();

                Object selected = JOptionPane.showInputDialog(null, "Proszę wybrać port pod którym podłączona jest kasa", "Wybór portu", JOptionPane.QUESTION_MESSAGE, null, ports, "0");
                if (selected != null) {//null if the user cancels.
                    String selectedString = selected.toString();
                    CashRegisterCommandProcessor.runGetParsedData(positionsPanel, selectedString);
                } else {
                        //CashRegisterCommandProcessor.runGetParsedData(positionsPanel, "COM12");
                    System.out.println("User cancelled");
                }

            }
        });

        parseDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ports[] = BarcodeScanner.getSerialPortNames();

                Object selected = JOptionPane.showInputDialog(null, "Proszę wybrać port pod którym podłączona jest kasa", "Wybór portu", JOptionPane.QUESTION_MESSAGE, null, ports, "0");
                if (selected != null) {//null if the user cancels.
                    String selectedString = selected.toString();
                    CashRegisterCommandProcessor.runSaveDatabase(selectedString);
                } else {
                    //CashRegisterCommandProcessor.runSaveDatabase("COM13");
                    System.out.println("User cancelled");
                }
            }
        });
        scannerPortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScannerPortQuestion();
            }
        });
    }

    public void enableWaitingState() {
        parseDataButton.setEnabled(false);
        showDataButton.setEnabled(false);
        databaseButton.setEnabled(false);
        scannerPortButton.setEnabled(false);
        progressBar.setVisible(true);

        waitLabel.setText("Oczekiwanie na odpowiedz z kasy...");
    }

    public void disableWaitingState() {
        parseDataButton.setEnabled(true);
        showDataButton.setEnabled(true);
        databaseButton.setEnabled(true);
        scannerPortButton.setEnabled(true);
        progressBar.setVisible(false);

        waitLabel.setText("");
    }

    public void showScannerPortQuestion() {
        String ports[] = BarcodeScanner.getSerialPortNames();

        Object selected = JOptionPane.showInputDialog(null, "Proszę wybrać port pod którym podłączony jest czytnik", "Wybór portu", JOptionPane.INFORMATION_MESSAGE, null, ports, "0");
        if (selected != null) {//null if the user cancels.
            toScanner.add(BarcodeScanner.INITIALIZE + ":" + selected);
        } else {
            System.out.println("User cancelled");
        }
    }

    public void goToPositionsPanel() {
        CardLayout cl = (CardLayout) cardLayout.getLayout();
        cl.show(cardLayout, "positionsPanel");
    }
}

