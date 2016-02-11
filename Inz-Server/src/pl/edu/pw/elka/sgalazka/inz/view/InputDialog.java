package pl.edu.pw.elka.sgalazka.inz.view;

import pl.edu.pw.elka.sgalazka.inz.database.Product;
import pl.edu.pw.elka.sgalazka.inz.view.inputValidation.BarcodeValidator;
import pl.edu.pw.elka.sgalazka.inz.view.inputValidation.NameValidator;
import pl.edu.pw.elka.sgalazka.inz.view.inputValidation.PriceValidator;
import pl.edu.pw.elka.sgalazka.inz.view.inputValidation.QuantityValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by gałązka on 2016-01-10.
 */
public class InputDialog extends JDialog {

    private JTextField name;
    private JTextField barcode;
    private JTextField quantity;
    private JTextField price;
    private JComboBox<String> vatGroup;
    private JCheckBox packaging;

    private JLabel nameLabel;
    private JLabel barcodeLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private JLabel vatGroupLabel;

    private JLabel nameErrorLabel;
    private JLabel barcodeErrorLabel;
    private JLabel quantityErrorLabel;
    private JLabel priceErrorLabel;

    private JPanel namePanel;
    private JPanel barcodePanel;
    private JPanel quantityPanel;
    private JPanel pricePanel;

    private JButton okButton;
    private JButton cancelButton;

    private boolean result;
    private static final Object lock = new Object();

    public InputDialog(JFrame jFrame) {
        super(jFrame, "Edycja towaru", true);
        initializeLabels();
        JPanel inner = new JPanel(new GridLayout(13, 1));
        namePanel = new JPanel(new GridBagLayout());
        barcodePanel = new JPanel(new GridBagLayout());
        quantityPanel = new JPanel(new GridBagLayout());
        pricePanel = new JPanel(new GridBagLayout());

        initializeControls();

        setSize(new Dimension(350, 500));
        setMinimumSize(new Dimension(350,500));
        setLayout(new BorderLayout());

        initializeNamePanel();
        initializeBarcodePanel();
        initializeQuantityPanel();
        initializePricePanel();

        inner.add(namePanel);
        inner.add(name);
        inner.add(barcodePanel);
        inner.add(barcode);
        inner.add(quantityPanel);
        inner.add(quantity);
        inner.add(pricePanel);
        inner.add(price);
        inner.add(vatGroupLabel);
        inner.add(vatGroup);
        inner.add(packaging);
        inner.add(okButton);
        inner.add(cancelButton);

        add(inner, BorderLayout.CENTER);

        initializeVerifiers();
        addListeners();
    }

    public InputDialog(JFrame jFrame, Product product) {
        this(jFrame);
        name.setText(product.getName());

        barcode.setText(product.getBarcode());
        quantity.setText(product.getQuantity() + "");
        double tmp = Double.parseDouble(product.getPrice() + "");
        tmp /= 100;
        price.setText((tmp + "").replace('.',','));
        vatGroup.setSelectedIndex(product.getVat().charAt(0) - 65);
        packaging.setSelected(product.getPackaging() != 0);
    }


    public boolean showDialog() {
        this.setVisible(true);
        return result;
    }

    private void initializeLabels() {
        nameLabel = new JLabel("Nazwa");
        barcodeLabel = new JLabel("Kod kreskowy");
        quantityLabel = new JLabel("Ilosc");
        priceLabel = new JLabel("Cena");
        vatGroupLabel = new JLabel("Grupa VAT");

        priceErrorLabel = new JLabel(" ");
        nameErrorLabel = new JLabel(" ");
        barcodeErrorLabel = new JLabel(" ");
        quantityErrorLabel = new JLabel(" ");

    }

    public String getNameValue() {
        return name.getText();
    }

    public String getBarcodeValue() {
        return barcode.getText();
    }

    public int getQuantityValue() {
        return Integer.parseInt(quantity.getText());
    }

    public int getPriceValue() {
        BigDecimal tmp = new BigDecimal(price.getText());
        tmp = tmp.multiply(new BigDecimal(100));
        return  tmp.intValue();
    }

    public char getVatGroupValue() {
        return (char) (vatGroup.getSelectedIndex() + 65);
    }

    public boolean getPackagingValue() {
        return packaging.isSelected();
    }

    class WA extends WindowAdapter {
        public void windowClosing(WindowEvent ev) {
            result = false;
            InputDialog.this.setVisible(false);
            InputDialog.this.dispose();
        }
    }

    private boolean checkFields() {
        return checkBarcode() && checkName() && checkPrice() && checkQuantity();
    }

    private boolean checkName() {
        if (name.getText().length() > 18) {
            JOptionPane.showMessageDialog(null, "Nazwa musi być krótsza niż 19 znaków", "Błąd",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkBarcode() {
        if (barcode.getText().length() != 13) {
            JOptionPane.showMessageDialog(null, "Kod kreskowy musi mieć 13 znaków", "Błąd",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;

    }

    private boolean checkQuantity() {
        if (quantity.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Proszę wpisać ilość", "Błąd",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkPrice() {

        if (price.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Proszę wpisać cenę", "Błąd",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            if (price.getText().contains(",")) {
                String temp = price.getText().replace(',', '.');
                price.setText(temp);
            }

            if (Double.parseDouble(price.getText()) == 0) {
                JOptionPane.showMessageDialog(null, "Cena nie może wynosić 0", "Błąd",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void initializeNamePanel(){
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        namePanel.add(nameLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        namePanel.add(nameErrorLabel, c);
    }

    private void initializeBarcodePanel(){
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        barcodePanel.add(barcodeLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        barcodePanel.add(barcodeErrorLabel, c);
    }

    private void initializeQuantityPanel(){
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        quantityPanel.add(quantityLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        quantityPanel.add(quantityErrorLabel, c);
    }

    private void initializePricePanel(){
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        pricePanel.add(priceLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        pricePanel.add(priceErrorLabel, c);
    }

    private void initializeControls(){
        NumberFormat intFormat = DecimalFormat.getInstance(Locale.GERMAN);
        intFormat.setMaximumFractionDigits(0);

        NumberFormat doubleFormat = DecimalFormat.getInstance();

        intFormat.setGroupingUsed(false);
        name = new JTextField(18);
        barcode = new JFormattedTextField(intFormat);
        quantity = new JFormattedTextField(intFormat);
        price = new JFormattedTextField(doubleFormat);
        packaging = new JCheckBox("Jest opakowaniem");

        String tab[] = {"A", "B", "C", "D", "E", "F", "G"};
        vatGroup = new JComboBox<String>(tab);
        vatGroup.setSelectedIndex(0);

        okButton = new JButton("Ok");
        cancelButton = new JButton("Anuluj");
    }

    private void initializeVerifiers(){
        name.setInputVerifier(new NameValidator(name, nameErrorLabel));
        barcode.setInputVerifier(new BarcodeValidator(barcode, barcodeErrorLabel));
        quantity.setInputVerifier(new QuantityValidator(quantity, quantityErrorLabel));
        price.setInputVerifier(new PriceValidator(price, priceErrorLabel));
    }

    private void addListeners(){
        okButton.addActionListener(e -> {
            if (checkFields()) {
                result = true;
                InputDialog.this.setVisible(false);
                InputDialog.this.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = false;
                InputDialog.this.setVisible(false);
                InputDialog.this.dispose();
            }
        });
        addWindowListener(new WA());
    }
}

