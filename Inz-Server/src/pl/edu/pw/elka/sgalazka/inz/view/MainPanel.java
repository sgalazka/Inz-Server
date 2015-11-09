package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ga³¹zka on 2015-10-07.
 */
public class MainPanel extends JPanel {

    private JButton downloadDataButton;
    private JButton uploadDataButton;
    private JButton databaseButton;
    private JPanel grid;
    MainPanel(JPanel cardLayout ){

        JPanel inner = new JPanel(new GridLayout(3,1));
        downloadDataButton = new JButton("Pobierz dane z kasy");
        uploadDataButton = new JButton("Zapisz dane do kasy");
        databaseButton = new JButton("Zarzadzaj baza towarow");

        downloadDataButton.setSize(150, 35);
        uploadDataButton.setSize(150, 35);
        databaseButton.setSize(150, 35);

        grid = new JPanel(new BorderLayout());
        inner.add(downloadDataButton);
        inner.add(uploadDataButton);
        inner.add(databaseButton);
        grid.add(inner, BorderLayout.CENTER);

        add(grid, BorderLayout.CENTER);

        databaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });
    }
}
