package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ga³¹zka on 2015-10-07.
 */
public class MainWindow implements Runnable{
    private JFrame mainFrame;
    private LogPanel logPanel;
    private MainPanel mainPanel;
    private BlockingQueue<LogType> queue;
    private JPanel cards;
    private MainDatabasePanel databasePanel;
    private ShowAllPanel showAllPanel;
    private AddProductPanel addProductPanel;

    public MainWindow(BlockingQueue<LogType> queue){
        this.queue = queue;
        cards = new JPanel(new CardLayout());
        mainFrame = new JFrame("Inz-Server");
        mainFrame.setLayout(new BorderLayout());
        logPanel = new LogPanel();
        mainPanel = new MainPanel(cards);
        databasePanel = new MainDatabasePanel(cards);
        showAllPanel = new ShowAllPanel(cards);
        addProductPanel = new AddProductPanel(cards);
        mainFrame.setSize(700,700);
        mainFrame.setBackground(new Color(40));
        mainFrame.add(logPanel, BorderLayout.LINE_END);
        cards.add(mainPanel, "mainPanel");
        cards.add(databasePanel, "databasePanel");
        cards.add(showAllPanel, "showAllPanel");
        cards.add(addProductPanel, "addProductPanel");


        mainFrame.add(cards, BorderLayout.LINE_START);
        mainFrame.setSize(1200,700);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

    }

    @Override
    public void run() {
        while(true) {
            if(!queue.isEmpty()){
                try {
                    LogType logType = queue.take();
                    if(logType.getType()=='I')
                        logPanel.addText("INFO:/t"+logType.getMessage());
                    else if(logType.getType()=='W')
                        logPanel.addText("WARNING:/t"+logType.getMessage());
                    else if(logType.getType()=='E')
                        logPanel.addText("ERROR:/t"+logType.getMessage());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
