package pl.edu.pw.elka.sgalazka.inz.view.inputValidation;

import javax.swing.*;

/**
 * Created by gałązka on 2016-01-30.
 */
public class NameValidator extends AbstractValidator {
    public NameValidator(JComponent c, JLabel errorLabel) {
        super(c, errorLabel);
    }

    @Override
    protected boolean validationCriteria(JComponent c) {
        JTextField tf = (JTextField) c;
        String tmp = tf.getText();
        if(tmp.isEmpty()){
            setMessage("Wpisz nazwę");
            return false;
        }
        else if (tmp.length()>18){
            setMessage("Nazwa musi mieć mniej niż 18 znaków");
            return false;
        }
        return true;
    }
}
