package pl.edu.pw.elka.sgalazka.inz.serial.commands;

import pl.edu.pw.elka.sgalazka.inz.serial.DLLFunctions;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2016-02-11.
 */
public class GetVatGroupsCommand extends CashRegisterCommand {

    public final static String NOTIFY_VAT = "notifyVat";

    public GetVatGroupsCommand(BlockingQueue<String> queue, String portName) {
        super(queue, portName);
    }

    @Override
    void process() {
        toView.add(WAIT);
        modifyConfigFile(portName);
        modifyInputFile();

        DLLFunctions.readVatGroups();

        FileDescriptor fd = openFileToRead(DLLFunctions.OUTPUT_FILE_NAME);
        if (fd == null) {
            toView.add(NO_FILE_ERROR);
            return;
        }
        String myLine = null;

        List<List<String>> list = new LinkedList<List<String>>();
        String tmp[] = null;

        try {
            if (fd.bufferedReader != null) {
                while ((myLine = fd.bufferedReader.readLine()) != null) {
                    if (myLine.charAt(0) == '$') {
                        tmp = myLine.split("\t");
                        break;
                    }
                }
                fd.fileInputStream.close();
                fd.bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tmp != null && tmp.length != 0) {
            String conc = new StringBuilder(NOTIFY_VAT)
                    .append(":").append(tmp[0].substring(1, tmp[0].length()))
                    .append(":").append(tmp[1])
                    .append(":").append(tmp[2])
                    .append(":").append(tmp[3])
                    .append(":").append(tmp[4])
                    .append(":").append(tmp[5])
                    .append(":").append(tmp[6]).toString();
            toView.add(conc);
        } else
            toView.add(NO_FILE_ERROR);
    }
}
