package pl.edu.pw.elka.sgalazka.inz.server.view.inputValidation;

import javax.swing.*;

/**
 * Created by gałązka on 2016-01-30.
 */
public class PriceValidator extends AbstractValidator {

    public PriceValidator(JComponent c, JLabel errorLabel) {
        super(c, errorLabel);
    }

    @Override
    protected boolean validationCriteria(JComponent c) {
        JTextField tf = (JTextField) c;
        String tmp = tf.getText();
        if(tmp.isEmpty()){
            setMessage("Wpisz cenę");
            return false;
        }
        if (tmp.contains(".")) {
            tmp = tmp.replace('.', ',');
            tf.setText(tmp);
        }
        if (tmp.matches("^\\d{6}\\d+$")) {
            setMessage("Maksymalna wartość to 999 999,99");
            return false;
        } else if (tmp.matches("^\\d{1,6},$")) {
            return true;
        } else if (tmp.matches("^\\d{1,6},\\d{2}\\d+$")) {
            setMessage("Maksymalnie dwa miejsca po przecinku");
            return false;
        } else if (!tmp.matches("^\\d{1,6}(,\\d{1,2})?$")) {
            setMessage("Niedozwolone znaki");
            return false;
        }
        return true;
    }
}
