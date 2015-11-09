package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ga³¹zka on 2015-10-07.
 */
public class LogPanel extends JPanel{

    private JTextArea logs;
    private JLabel logLabel;

    LogPanel(){
        logs = new JTextArea(50, 30);
        logLabel = new JLabel("LOGS:");

        /*JScrollPane scrollPane = new JScrollPane(logs);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        GridBagConstraints c2 = new GridBagConstraints();
        c2.weighty = 2;
        c2.weightx = 0;

        add(scrollPane, c);
        add(logLabel, c2);*/

        setLayout(new BorderLayout());
        add(logLabel, BorderLayout.NORTH);
        add(logs, BorderLayout.CENTER);
    }
    void addText(String log){
        logs.append(log + "\n");
    }
}
