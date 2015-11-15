package pl.edu.pw.elka.sgalazka.inz.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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


    MainDatabasePanel(JPanel cardLayout) {

        //setLayout(new BorderLayout());
        JPanel inner = new JPanel(new GridLayout(5,1));
        JPanel grid = new JPanel(new BorderLayout());
        addButton = new JButton("Dodaj towar");
        deleteButton = new JButton("Usun towar");
        updateButton = new JButton("Modyfikuj dane towaru");
        showButton = new JButton("Pokaz towary");
        backButton = new JButton("Wroc");

        addButton.setSize(150, 35);
        deleteButton.setSize(150, 35);
        updateButton.setSize(150, 35);
        showButton.setSize(150, 35);
        backButton.setSize(150, 35);

        inner.add(addButton);
        inner.add(deleteButton);
        inner.add(updateButton);
        inner.add(showButton);
        inner.add(backButton);

        grid.add(inner, BorderLayout.CENTER);
        add(grid, BorderLayout.CENTER);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "mainPanel");
            }
        });
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "showAllPanel");
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "addProductPanel");
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "deletePanel");
            }
        });
    }
}
