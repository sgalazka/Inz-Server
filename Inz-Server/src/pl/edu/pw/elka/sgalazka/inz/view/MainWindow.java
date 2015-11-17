package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;

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
    private LogPanel logPanel;
    private MainPanel mainPanel;
    private BlockingQueue<LogType> queue;
    private JPanel cards;
    private MainDatabasePanel databasePanel;
    private ShowAllPanel showAllPanel;
    private AddProductPanel addProductPanel;
    private DeletePanel deletePanel;

    public MainWindow(BlockingQueue<LogType> queue) {
        this.queue = queue;
        cards = new JPanel(new CardLayout());
        mainFrame = new JFrame("Inz-Server");
        mainFrame.setLayout(new BorderLayout());
        logPanel = new LogPanel();
        mainPanel = new MainPanel(cards);
        databasePanel = new MainDatabasePanel(cards);
        showAllPanel = new ShowAllPanel(cards);
        deletePanel = new DeletePanel(cards);
        addProductPanel = new AddProductPanel(cards, deletePanel.getTableModel(), showAllPanel.getTableModel());
        mainFrame.setSize(new Dimension(900, 700));
        mainFrame.setMinimumSize(new Dimension(750, 300));
        mainFrame.setBackground(new Color(40));
        mainFrame.add(logPanel, BorderLayout.EAST);
        cards.add(mainPanel, "mainPanel");
        cards.add(databasePanel, "databasePanel");
        cards.add(showAllPanel, "showAllPanel");
        cards.add(addProductPanel, "addProductPanel");
        cards.add(deletePanel, "deletePanel");


        mainFrame.add(cards, BorderLayout.CENTER);
        mainFrame.setSize(1200, 700);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closed");
                DatabaseManager.getInstance().endTransaction();
                e.getWindow().dispose();
            }
        });

    }

    @Override
    public void run() {
        while (true) {
            //if (!queue.isEmpty()) {
                try {
                    LogType logType = queue.take();
                    if (logType.getType() == 'I')
                        logPanel.addText("INFO:       " + logType.getMessage(), logType.getType());
                    else if (logType.getType() == 'W')
                        logPanel.addText("WARNING:" + logType.getMessage(), logType.getType());
                    else if (logType.getType() == 'E')
                        logPanel.addText("ERROR:  " + logType.getMessage(), logType.getType());
                    else if (logType.getType() == 'N')
                        databasePanel.notifyChange();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            //}
        }
    }
}
