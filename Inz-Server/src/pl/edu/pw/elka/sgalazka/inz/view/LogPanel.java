package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ga³¹zka on 2015-10-07.
 */
public class LogPanel extends JPanel{


    private JLabel logLabel;
    //private JTextArea logs;
    private JTextPane logs;
    private DefaultStyledDocument document;
    private Style style;


    LogPanel(){
        //logs = new JTextArea(50, 30);
        document = new DefaultStyledDocument();;
        logs = new JTextPane(document);

        logs.setMaximumSize(new Dimension(150,200));

        logLabel = new JLabel("LOGS:");
        StyleContext context = new StyleContext();
        style = context.addStyle("test", null);



        /*JScrollPane scrollPane = new JScrollPane(logs,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 0, 500, 500);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;*/

        /*GridBagConstraints c2 = new GridBagConstraints();
        c2.weighty = 2;
        c2.weightx = 0;/*

        scrollPane.setMinimumSize(new Dimension(450, 200));
        add(scrollPane, c);
        add(logLabel, c2);*/

        setLayout(new BorderLayout());
        add(logLabel, BorderLayout.NORTH);
        add(logs, BorderLayout.CENTER);
    }
    void addText(String log, char type){
        //logs.append(log + "\n");
        switch(type){
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
        //logs.setText(logs.getText()+ log+ "\n");
    }
}
