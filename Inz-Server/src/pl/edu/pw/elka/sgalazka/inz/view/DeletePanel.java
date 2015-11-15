package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gałązka on 2015-11-14.
 */
public class DeletePanel extends JPanel{

    private JButton button;
    private String columns[] = {"id", "barcode", "quantity", "name"};
    private DefaultTableModel tableModel;
    private JTable jTable;

    DeletePanel(JPanel cardLayout) {

        jTable = new JTable();

        setLayout(new BorderLayout());
        button = new JButton("wroc");

        fillJTable();

        JScrollPane scrollPane = new JScrollPane(jTable);

        add(scrollPane, BorderLayout.CENTER);
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                Log.w("tableChanged");
                fillJTable();
                jTable.repaint();
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                super.mouseClicked(evt);
                int row = jTable.rowAtPoint(evt.getPoint());
                int col = jTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    int toDelete = Integer.parseInt((String) jTable.getModel().getValueAt(row, 0));

                    int decision = JOptionPane.showConfirmDialog(null,"Czy na pewno chcesz usunąć produkt o nazwie" +
                            jTable.getModel().getValueAt(row, 3)+"?","Potwierdzenie",JOptionPane.YES_NO_OPTION);

                    Log.d("Option:"+decision);
                    if(decision==JOptionPane.OK_OPTION)
                        DatabaseManager.getInstance().delete(toDelete);

                    tableModel.fireTableDataChanged();
                    Log.d("to delete:"+toDelete);
                }
            }
        });
        add(button, BorderLayout.SOUTH);
    }

    private void fillJTable(){
        java.util.List<Product> list = DatabaseManager.getInstance().getAllProducts();
        tableModel = new DefaultTableModel(columns, 0);
        for (Product p : list) {
            String temp[] = new String[4];
            temp[0] = p.getCode() + "";
            temp[1] = p.getBarcode();
            temp[2] = p.getQuantity() + "";
            temp[3] = p.getName();
            tableModel.addRow(temp);
        }
        jTable.setModel(tableModel);
    }
    public DefaultTableModel getTableModel(){
        return tableModel;
    }
}
