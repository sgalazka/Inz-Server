package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by ga³¹zka on 2015-10-16.
 */
public class ShowAllPanel extends JPanel {
    private JTable jTable;
    private JButton button;
    private String columns[] = {"id", "barcode", "quantity", "name"};
    private DefaultTableModel tableModel;

    ShowAllPanel(JPanel cardLayout){
        tableModel = new DefaultTableModel(columns,0);

        setLayout(new BorderLayout());
        button = new JButton("wroc");
        List<Product> list = DatabaseManager.getInstance().getAllProducts();

        for(Product p : list){
            String temp[] = new String[4];
            temp[0] = p.getCode()+"";
            temp[1] = p.getBarcode();
            temp[2] = p.getQuantity()+"";
            temp[3] = p.getName();
            tableModel.addRow(temp);
        }
        jTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(jTable);

        add(scrollPane, BorderLayout.CENTER);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });
        add(button, BorderLayout.SOUTH);
    }
}
