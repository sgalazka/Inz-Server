package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ga³¹zka on 2015-10-10.
 */
public class MainDatabasePanel extends JPanel {

    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton showButton;
    private JButton backButton;
    MainDatabasePanel(JPanel cardLayout){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addButton = new JButton("Dodaj towar");
        deleteButton = new JButton("Usun towar");
        updateButton = new JButton("Modyfikuj dane towaru");
        showButton = new JButton("Pokaz towary");
        backButton = new JButton("Wroc");
        add(addButton);
        add(deleteButton);
        add(updateButton);
        add(showButton);
        add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "mainPanel");
            }
        });
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "showAllPanel");
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "addProductPanel");
            }
        });
    }
}
