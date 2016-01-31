package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by gałązka on 2016-01-02.
 */
public class PositionsPanel extends JPanel implements DataReceiver {

    private final static String columns[] = {"Nazwa", "Kod kreskowy", "VAT", "Cena", "Opakowanie" };
    private NonEditableTableModel tableModel;
    private JTable jTable;
    private JButton backButton;

    public PositionsPanel(JPanel cardLayout) {
        jTable = new JTable();
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new BorderLayout());

        //fillJTable();

        backButton = new JButton("Wroc");
        JScrollPane scrollPane = new JScrollPane(jTable);

        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "mainPanel");
            }
        });
    }

    public boolean showData(List<List<String>> list) {
        Log.d("PositionsPanel showdata");
        if(list == null){
            return false;
        }
        tableModel = new NonEditableTableModel(columns, 0);
        for (List<String> l : list) {
            tableModel.addRow(l.toArray());
        }
        jTable.setModel(tableModel);
        return true;
    }

    @Override
    public void onDataReceived(List<List<String>> data) {
        showData(data);
    }

}
