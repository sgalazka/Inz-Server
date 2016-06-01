package pl.edu.pw.elka.sgalazka.inz.server.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gałązka on 2016-02-01.
 */
public class VatGroupsDialog extends JDialog {

    private JTable jTable;
    private JButton backButton;
    private NonEditableTableModel tableModel;
    private String columns[] = {"Grupa", "Stawka"};

    VatGroupsDialog(JFrame jFrame, String groups){
        super(jFrame,"Grupy VAT",true);
        JPanel jPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Wróć");

        jTable = new JTable();
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jPanel.add(new JScrollPane(jTable), BorderLayout.CENTER);
        jPanel.add(backButton, BorderLayout.SOUTH);
        add(jPanel);
        showData(groups);
        setSize(new Dimension(350, 300));
        setMinimumSize(new Dimension(350, 300));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VatGroupsDialog.this.setVisible(false);
                VatGroupsDialog.this.dispose();
            }
        });

        setVisible(true);
    }

    private void showData(String groups){
        String tmp[] = groups.split(":");

        tableModel = new NonEditableTableModel(columns,0);

        tableModel.addRow(new String[]{"A", tmp[1]});
        tableModel.addRow(new String[]{"B", tmp[2]});
        tableModel.addRow(new String[]{"C", tmp[3]});
        tableModel.addRow(new String[]{"D", tmp[4]});
        tableModel.addRow(new String[]{"E", tmp[5]});
        tableModel.addRow(new String[]{"F", tmp[6]});
        tableModel.addRow(new String[]{"G", tmp[7]});
        jTable.setModel(tableModel);
    }
}
