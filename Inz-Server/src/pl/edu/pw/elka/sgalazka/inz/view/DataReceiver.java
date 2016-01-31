package pl.edu.pw.elka.sgalazka.inz.view;

import java.util.List;

/**
 * Created by gałązka on 2016-01-04.
 */
public interface DataReceiver {
    void onDataReceived(List<List<String>> data);
}
