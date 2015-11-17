package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ga��zka on 2015-10-10.
 */
public class MainDatabasePanel extends JPanel {

    private final Object lock = new Object();
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;

    private JButton backButton;
    private JPanel gridButtons;
    private String columns[] = {"id", "barcode", "quantity", "name"};
    private NonEditableTableModel tableModel;
    private JTable jTable;

    MainDatabasePanel(JPanel cardLayout) {
        gridButtons = new JPanel(new GridLayout(1, 3));
        setLayout(new BorderLayout());
        jTable = new JTable();
        tableModel = new NonEditableTableModel(columns, 0);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fillJTable();

        addButton = new JButton("Dodaj towar");
        deleteButton = new JButton("Usun towar");
        updateButton = new JButton("Modyfikuj dane towaru");

        backButton = new JButton("Wroc");

        JScrollPane scrollPane = new JScrollPane(jTable);


        gridButtons.add(addButton);
        gridButtons.add(deleteButton);
        gridButtons.add(updateButton);

        gridButtons.add(backButton);

        add(gridButtons, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                //Log.w(jTable.getValueAt(jTable.getSelectedRow(), 0).toString());
                System.out.println(event.toString());
            }
        });
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                Log.w("tableChanged");
                fillJTable();
                jTable.repaint();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) cardLayout.getLayout();
                cl.show(cardLayout, "mainPanel");
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
                decimalFormat.setGroupingUsed(false);
                JTextField name = new JTextField();
                JTextField code = new JFormattedTextField(decimalFormat);
                JTextField barcode = new JFormattedTextField(decimalFormat);
                JTextField quantity = new JFormattedTextField(decimalFormat);

                final JComponent[] inputs = new JComponent[]{
                        new JLabel("Nazwa"),
                        name,
                        new JLabel("Kod"),
                        code,
                        new JLabel("Kod kreskowy"),
                        barcode,
                        new JLabel("Ilość"),
                        quantity
                };
                //JOptionPane.showMessageDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);

                int decision = JOptionPane.showConfirmDialog(null, inputs, "Dodaj towar do bazy"
                        , JOptionPane.YES_NO_OPTION);
                if (decision == JOptionPane.OK_OPTION) {
                    if(name.getText().isEmpty() || code.getText().isEmpty() ||
                            barcode.getText().isEmpty() || quantity.getText().isEmpty()){
                        JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    Product product = new Product();
                    product.setName(name.getText());
                    product.setCode(Integer.parseInt(code.getText()));
                    product.setBarcode(barcode.getText());
                    product.setQuantity(Integer.parseInt(quantity.getText()));
                    if (DatabaseManager.getInstance().add(product)) {
                        notifyChange();
                        //tableModel.fireTableDataChanged();
                        JOptionPane.showMessageDialog(null, "Dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);
                        Log.d("successful added to database");
                    } else {
                        JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jTable.getSelectedRow() >= 0) {
                    int decision = JOptionPane.showConfirmDialog(null,
                            "Czy na pewno chcesz usunąć produkt o nazwie" +
                                    jTable.getValueAt(jTable.getSelectedRow(), 0).toString() + "?",
                            "Potwierdzenie", JOptionPane.YES_NO_OPTION);

                    Log.d("Option:" + decision);
                    if (decision == JOptionPane.OK_OPTION) {
                        if (DatabaseManager.getInstance().
                                delete(jTable.getValueAt(jTable.getSelectedRow(), 1).toString() ) ) {
                            notifyChange();
                            //tableModel.fireTableDataChanged();
                            JOptionPane.showMessageDialog(null, "Usunięta", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Błąd!\nNie usunięto", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    Log.d("to delete:" + jTable.getSelectedRow());
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
                decimalFormat.setGroupingUsed(false);
                JTextField name = new JTextField();
                JTextField code = new JFormattedTextField(decimalFormat);
                JTextField barcode = new JFormattedTextField(decimalFormat);
                JTextField quantity = new JFormattedTextField(decimalFormat);
                name.setText(jTable.getValueAt(jTable.getSelectedRow(), 3).toString());
                code.setText(jTable.getValueAt(jTable.getSelectedRow(), 0).toString());
                barcode.setText(jTable.getValueAt(jTable.getSelectedRow(), 1).toString());
                quantity.setText(jTable.getValueAt(jTable.getSelectedRow(), 2).toString());

                final JComponent[] inputs = new JComponent[]{
                        new JLabel("Nazwa"),
                        name,
                        new JLabel("Kod"),
                        code,
                        new JLabel("Kod kreskowy"),
                        barcode,
                        new JLabel("Ilość"),
                        quantity
                };
                //JOptionPane.showMessageDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);

                int decision = JOptionPane.showConfirmDialog(null, inputs, "Zmień dane"
                        , JOptionPane.YES_NO_OPTION);
                if (decision == JOptionPane.OK_OPTION) {
                    if(name.getText().isEmpty() || code.getText().isEmpty() ||
                            barcode.getText().isEmpty() || quantity.getText().isEmpty()){
                        JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    Product product = new Product();
                    product.setName(name.getText());
                    product.setCode(Integer.parseInt(code.getText()));
                    product.setBarcode(barcode.getText());
                    product.setQuantity(Integer.parseInt(quantity.getText()));
                    if (DatabaseManager.getInstance().update(product)) {

                        //tableModel.fireTableDataChanged();
                        notifyChange();
                        JOptionPane.showMessageDialog(null, "Dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }
        });
    }

    private void fillJTable() {
        java.util.List<Product> list = DatabaseManager.getInstance().getAllProducts();
        tableModel = new NonEditableTableModel(columns, 0);
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

    private static BlockingQueue<Object> notifyQueue= new LinkedBlockingQueue<>();
    private Runnable notifyListener = new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    notifyQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notifyChange();
            }
        }
    };

    public void notifyChange(){

            tableModel.fireTableDataChanged();

    }

    public static void fireNotify(){
        try {
            notifyQueue.put(new Object());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
