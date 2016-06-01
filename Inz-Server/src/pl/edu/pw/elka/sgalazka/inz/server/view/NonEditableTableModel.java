package pl.edu.pw.elka.sgalazka.inz.server.view;

import javax.swing.table.DefaultTableModel;

/**
 * Created by gałązka on 2015-11-15.
 */
public class NonEditableTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public NonEditableTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }
}
