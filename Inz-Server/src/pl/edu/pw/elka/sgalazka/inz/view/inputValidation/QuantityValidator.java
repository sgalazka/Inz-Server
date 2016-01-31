package pl.edu.pw.elka.sgalazka.inz.view.inputValidation;

import javax.swing.*;

/**
 * Created by gałązka on 2016-01-31.
 */
public class QuantityValidator extends AbstractValidator  {
    public QuantityValidator(JComponent c, JLabel errorLabel) {
        super(c, errorLabel);
    }

    @Override
    protected boolean validationCriteria(JComponent c) {
        JTextField tf = (JTextField) c;
        String tmp = tf.getText();
        if(tmp.isEmpty()){
            setMessage("Wpisz ilość");
            return false;
        }
        else if (!tmp.matches("^\\d+$")){
            setMessage("Niedozwolone znaki");
            return false;
        }
        else if (!tmp.matches("^\\d{1,10}$")){
            setMessage("Maksymalna ilość to 9 999 999 999");
            return false;
        }
        return true;
    }
}
