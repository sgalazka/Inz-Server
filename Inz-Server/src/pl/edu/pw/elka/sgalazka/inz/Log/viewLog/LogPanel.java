package pl.edu.pw.elka.sgalazka.inz.Log.viewLog;

import pl.edu.pw.elka.sgalazka.inz.Log.LogType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class LogPanel extends JPanel implements Runnable {

    private static final String ERRORS = "Błędy";
    private static final String WARNS = "Ostrzeżenia";
    private static final String DEBUG = "Debugowanie";
    private static final String INFO = "Info";

    private static final String options[] = {ERRORS, WARNS, DEBUG, INFO};

    private JLabel logLabel;
    private JTextPane logs;
    private DefaultStyledDocument document;
    private Style style;
    private BlockingQueue<LogType> queue;
    private JPanel topPanel;
    private JPanel tittlePanel;
    private JComboBox<String> comboBox;
    private java.util.List<Tuple> logList;
    private JButton printButton;

    public LogPanel(BlockingQueue<LogType> toLog) {
        queue = toLog;
        topPanel = new JPanel();
        tittlePanel = new JPanel();
        printButton = new JButton("Zapisz do pliku");

        logList = new LinkedList<>();

        comboBox = new JComboBox<>(options);

        setLayout(new GridLayout());
        document = new DefaultStyledDocument();
        logs = new JTextPane(document);

        logLabel = new JLabel("LOGS:");
        StyleContext context = new StyleContext();
        style = context.addStyle("test", null);

        JScrollPane scrollPane = new JScrollPane(logs,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tittlePanel.add(logLabel, BorderLayout.WEST);
        tittlePanel.add(comboBox, BorderLayout.EAST);
        tittlePanel.setMaximumSize(new Dimension(500, 100));
        tittlePanel.setPreferredSize(new Dimension(450, 30));

        topPanel.setLayout(new BorderLayout());
        logLabel.setPreferredSize(new Dimension(310, 30));
        topPanel.add(tittlePanel, BorderLayout.PAGE_START);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(printButton, BorderLayout.PAGE_END);
        add(topPanel, BorderLayout.CENTER);

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBox.getSelectedItem();
                System.out.println("combo: " + selected);
                try {
                    document.remove(0, document.getLength());
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                if (logList.isEmpty())
                    return;
                for (Iterator<Tuple> it = logList.iterator(); it.hasNext(); ) {
                    addText(it.next());
                }
            }
        });
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printToFile(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    void addText(Tuple tuple) {

        char type = tuple.logType.getType();
        try {
            switch (type) {
                case 'E':
                    if (!comboBox.getSelectedItem().equals(ERRORS)) {
                        return;
                    }
                    StyleConstants.setForeground(style, Color.RED);
                    break;
                case 'W':
                    if (!comboBox.getSelectedItem().equals(WARNS) && !comboBox.getSelectedItem().equals(ERRORS)) {
                        return;
                    }
                    StyleConstants.setForeground(style, Color.ORANGE);
                    break;
                case 'D':
                    if (comboBox.getSelectedItem().equals(INFO)) {
                        return;
                    }
                    StyleConstants.setForeground(style, Color.BLACK);
                    break;
                case 'I':
                    StyleConstants.setForeground(style, Color.BLUE);
                    break;
            }

            document.insertString(document.getLength(), tuple.timestamp + " " + tuple.logType.getMessage() + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                LogType logType = queue.take();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                Date date = new Date();
                String temp = dateFormat.format(date);
                Tuple tuple = new Tuple(logType, temp);
                logList.add(tuple);
                addText(tuple);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printToFile(boolean isAutomatic) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
        Date date = new Date();
        String temp = dateFormat.format(date);
        File file = new File(".\\logs\\log_" + temp + ".txt");
        file.getParentFile().mkdirs();
        PrintWriter writer = new PrintWriter(file);
        if (logList.isEmpty())
            return;
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (!isAutomatic) {
            for (Iterator<Tuple> it = logList.iterator(); it.hasNext(); ) {
                Tuple tuple = it.next();
                writer.println(tuple.timestamp + " " + tuple.logType.getType() + " " + tuple.logType.getMessage() + "\n");
                writer.flush();
                it.remove();
            }
        }
        writer.close();
        queue.add(new LogType('I', "Zapisano logi do pliku"));
    }

    private static class Tuple {
        LogType logType;
        String timestamp;

        public Tuple(LogType l, String t) {
            logType = l;
            timestamp = t;
        }
    }

}
