package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * Created by ga��zka on 2015-10-10.
 */
public class ProductsPanel extends JPanel {

    private final static Object lock = new Object();
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton backButton;

    private JPanel gridButtons;
    private final static String columns[] = {"Nazwa", "Kod kreskowy", "Ilosc", "Cena", "Grupa VAT", "Opakowanie"};
    private NonEditableTableModel tableModel;
    private JTable jTable;
    public final static String DATA_CHANGED = "dataChanged";

    public ProductsPanel(JPanel cardLayout) {
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

                if (DatabaseManager.getInstance().getRowCount() > 4096) {
                    JOptionPane.showMessageDialog(null, "Baza jest pełna, aby dodać produkt należy usunąć jeden z nich", "Przepełnienie",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JFrame jFrame = new JFrame();
                    InputDialog inputDialog = new InputDialog(jFrame);
                    boolean result = inputDialog.showDialog();
                    if (result) {
                        System.out.println("dialog successful " + inputDialog.getVatGroupValue());
                        Product product = new Product();
                        product.setName(inputDialog.getNameValue());
                        product.setQuantity(inputDialog.getQuantityValue());
                        product.setBarcode(inputDialog.getBarcodeValue());
                        product.setPackaging(inputDialog.getPackagingValue() ? 1 : 0);
                        product.setPrice(inputDialog.getPriceValue());
                        product.setVat(inputDialog.getVatGroupValue() + "");

                        if (DatabaseManager.getInstance().add(product)) {
                            notifyChange();
                            JOptionPane.showMessageDialog(null, "Dodano do bazy", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                            Log.i("successful added to database");
                        } else {
                            JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
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

                    Log.i("Option:" + decision);
                    if (decision == JOptionPane.OK_OPTION) {
                        Log.i("to delete: " + jTable.getValueAt(jTable.getSelectedRow(), 1).toString());
                        String bcode = jTable.getValueAt(jTable.getSelectedRow(), 1).toString();
                        Product p = DatabaseManager.getInstance().findByBarcode(bcode);
                        long id = p.getId();
                        if (DatabaseManager.getInstance().delete(id)) {
                            notifyChange();
                            JOptionPane.showMessageDialog(null, "Usunięta", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Błąd!\nNie usunięto", "Question",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    Log.i("to delete:" + jTable.getSelectedRow());
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jTable.getSelectedRow() >= 0) {
                    Product tmp = DatabaseManager.getInstance().findByBarcode(jTable.getValueAt(jTable.getSelectedRow(), 1).toString());
                    if (tmp != null) {
                        JFrame jFrame = new JFrame();
                        InputDialog inputDialog = new InputDialog(jFrame, tmp);
                        if (inputDialog.showDialog()) {
                            System.out.println("Cena: " + inputDialog.getPriceValue());
                            tmp.setName(inputDialog.getNameValue());
                            tmp.setQuantity(inputDialog.getQuantityValue());
                            tmp.setBarcode(inputDialog.getBarcodeValue());
                            tmp.setPackaging(inputDialog.getPackagingValue() ? 1 : 0);
                            tmp.setPrice(inputDialog.getPriceValue());
                            tmp.setVat(inputDialog.getVatGroupValue() + "");

                            if (DatabaseManager.getInstance().update(tmp)) {
                                notifyChange();
                                JOptionPane.showMessageDialog(null, "Dodano do bazy", "Question",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Błąd!\nNie dodano do bazy", "Question",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
    }

    private void fillJTable() {
        java.util.List<Product> list = DatabaseManager.getInstance().getAllProducts();
        if (list == null || list.isEmpty())
            return;
        tableModel = new NonEditableTableModel(columns, 0);
        DecimalFormat df = new DecimalFormat("#.00");
        for (Product p : list) {
            String temp[] = new String[6];
            temp[0] = p.getName();
            temp[1] = p.getBarcode();
            temp[2] = p.getQuantity() + "";
            double tmp = p.getPrice();
            tmp /= 100;
            temp[3] = df.format(tmp) + "";
            temp[4] = p.getVat();
            temp[5] = p.getPackaging() == 1 ? "Tak" : "";
            tableModel.addRow(temp);
        }
        jTable.setModel(tableModel);
    }

    public void notifyChange() {
        synchronized (lock) {
            fillJTable();
            jTable.repaint();
            tableModel.fireTableDataChanged();
            Log.i("productsPanel notified");
        }
    }

}
