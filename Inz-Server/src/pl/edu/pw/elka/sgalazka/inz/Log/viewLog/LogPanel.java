package pl.edu.pw.elka.sgalazka.inz.Log.viewLog;

import pl.edu.pw.elka.sgalazka.inz.Log.LogType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ga��zka on 2015-10-07.
 */
public class LogPanel extends JPanel implements Runnable {

    private JLabel logLabel;
    private JTextPane logs;
    private DefaultStyledDocument document;
    private Style style;
    private BlockingQueue<LogType> queue;
    private JPanel topPanel;

    public LogPanel(BlockingQueue<LogType> toLog) {
        queue = toLog;
        topPanel = new JPanel();

        setLayout(new GridLayout());
        document = new DefaultStyledDocument();
        logs = new JTextPane(document);

        logLabel = new JLabel("LOGS:");
        StyleContext context = new StyleContext();
        style = context.addStyle("test", null);

        JScrollPane scrollPane = new JScrollPane(logs,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        topPanel.setLayout(new BorderLayout());
        topPanel.setMinimumSize(new Dimension(350, 0));
        logLabel.setPreferredSize(new Dimension(500, 30));
        logLabel.setMaximumSize(new Dimension(700, 30));
        topPanel.add(logLabel, BorderLayout.PAGE_START);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);
    }

    void addText(String log, char type) {

        switch (type) {
            case 'e':
            case 'E':
                StyleConstants.setForeground(style, Color.RED);
                break;
            case 'i':
            case 'I':
                StyleConstants.setForeground(style, Color.BLUE);
                break;
            case 'w':
            case 'W':
                StyleConstants.setForeground(style, Color.ORANGE);
                break;
        }

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:sss");
        Date date = new Date();
        String temp = dateFormat.format(date);
        try {
            document.insertString(document.getLength(), temp + " " + log + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                LogType logType = queue.take();
                if (logType.getType() == 'I')
                    addText("INFO :       " + logType.getMessage(), logType.getType());
                else if (logType.getType() == 'W')
                    addText("WARN :" + logType.getMessage(), logType.getType());
                else if (logType.getType() == 'E')
                    addText("ERROR:  " + logType.getMessage(), logType.getType());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
