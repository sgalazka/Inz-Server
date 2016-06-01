package pl.edu.pw.elka.sgalazka.inz.server.view.inputValidation;

import javax.swing.*;

/**
 * Created by gałązka on 2016-01-30.
 */
public class BarcodeValidator extends AbstractValidator  {
    public BarcodeValidator(JComponent c, JLabel errorLabel) {
        super(c, errorLabel);
    }

    @Override
    protected boolean validationCriteria(JComponent c) {
        JTextField tf = (JTextField) c;
        String tmp = tf.getText();
        if(tmp.isEmpty()){
            setMessage("Wpisz kod kreskowy");
            return false;
        }
        else if (!tmp.matches("^\\d+$")){
            setMessage("Niedozwolone znaki");
            return false;
        }
        else if(!tmp.matches("^\\d{13}$")){
            setMessage("Kod kreskowy musi mieć 13 znaków");
            return false;
        }
        return true;
    }
}
