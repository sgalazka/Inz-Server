package pl.edu.pw.elka.sgalazka.inz.serial.commands;

import pl.edu.pw.elka.sgalazka.inz.serial.DLLFunctions;
import pl.edu.pw.elka.sgalazka.inz.view.DataReceiver;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2016-02-11.
 */
public class GetPositionsCommand extends CashRegisterCommand {
    public final static String NOTIFY_PARSED = "notifyParsed";

    public GetPositionsCommand(BlockingQueue<String> queue, String portName, DataReceiver receiver) {
        super(queue, portName, receiver);
    }

    @Override
    void process() {
        toView.add(CashRegisterCommand.WAIT);

        modifyConfigFile(portName);
        modifyInputFile();

        /*try {
            DLLFunctions.readWareDatabase();
        } catch (UnsatisfiedLinkError e) {
            toView.add(CashRegisterCommand.NO_DLL_ERROR);
            dataReceiver.onDataReceived(null);
        }*/

        ReadFileDescriptor fd = openFileToRead(DLLFunctions.OUTPUT_FILE_NAME);
        if (fd == null) {
            toView.add(NO_FILE_ERROR);
            return;
        }
        String myLine = null;
        List<List<String>> list = new LinkedList<List<String>>();

        try {
            while ((myLine = fd.bufferedReader.readLine()) != null) {
                if (myLine.charAt(0) == '$' && myLine.length() > 50) {
                    String tab[] = myLine.split("\t");
                    List<String> inner = new LinkedList<String>();
                    char vat = (char) ((int) tab[2].charAt(0) + 17);
                    inner.add(tab[1]);
                    inner.add(tab[7]);
                    inner.add(vat + "");

                    BigDecimal tmp = new BigDecimal(tab[8]);
                    tmp = tmp.divide(new BigDecimal(100));
                    inner.add(tmp.setScale(2, RoundingMode.CEILING).toString());

                    inner.add(tab[9].equals("0") ? "" : "Tak");
                    list.add(inner);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fd.fileInputStream.close();
            fd.bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataReceiver.onDataReceived(list);
        toView.add(NOTIFY_PARSED);
    }
}
