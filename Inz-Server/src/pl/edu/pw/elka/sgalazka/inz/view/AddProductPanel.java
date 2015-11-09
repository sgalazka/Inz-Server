package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.database.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ga³¹zka on 2015-10-17.
 */
public class AddProductPanel extends JPanel {

    private JLabel nameLabel;
    private JLabel codeLabel;
    private JLabel barcodeLabel;
    private JLabel quantityLabel;

    private JTextField name;
    private JTextField code;
    private JTextField barcode;
    private JTextField quantity;

    private JButton enterButton;
    private JButton backButton;
    AddProductPanel(JPanel cardLayout){
        setLayout(new GridLayout(10,1));

        backButton = new JButton("wroc");
        enterButton = new JButton("wprowadz");

        nameLabel = new JLabel("Nazwa:");
        codeLabel = new JLabel("Kod:");
        barcodeLabel = new JLabel("Kod kreskowy:");
        quantityLabel = new JLabel("Ilosc:");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        name = new JTextField();
        code = new JFormattedTextField(decimalFormat);
        barcode = new JFormattedTextField(decimalFormat);
        quantity = new JFormattedTextField(decimalFormat);

        add(nameLabel);
        add(name);
        add(codeLabel);
        add(code);
        add(barcodeLabel);
        add(barcode);
        add(quantityLabel);
        add(quantity);
        add(enterButton);
        add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = new Product();
                product.setName(name.getText());
                product.setCode(Integer.parseInt(code.getText()));
                product.setBarcode(barcode.getText());
                product.setQuantity(Integer.parseInt(quantity.getText()));
                DatabaseManager.getInstance().add(product);
                JOptionPane.showMessageDialog(AddProductPanel.this, "Download completed", "Question",
                        JOptionPane.INFORMATION_MESSAGE);
                CardLayout cl = (CardLayout)cardLayout.getLayout();
                cl.show(cardLayout, "databasePanel");
            }
        });
    }
}
