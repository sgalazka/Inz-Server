package pl.edu.pw.elka.sgalazka.inz.view.inputValidation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class AbstractValidator extends InputVerifier implements KeyListener {

    private JLabel messageLabel;
    private JComponent toCheck;

    private AbstractValidator(JComponent c) {
        c.addKeyListener(this);
        toCheck = c;
    }

    public AbstractValidator(JComponent c, JLabel errorLabel) {
        this(c);
        messageLabel = errorLabel;
        toCheck = c;
    }

    protected abstract boolean validationCriteria(JComponent c);

    public boolean verify(JComponent c) {
        if (!validationCriteria(c)) {
            c.setBackground(Color.PINK);
            return false;
        }
        c.setBackground(Color.WHITE);
        messageLabel.setText(" ");
        return true;
    }

    protected void setMessage(String message) {
        messageLabel.setText("<html><font color='red'>" + message + "</font ></html >");
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        verify(toCheck);
    }

}