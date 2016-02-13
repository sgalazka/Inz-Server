package pl.edu.pw.elka.sgalazka.inz.serial;

import pl.edu.pw.elka.sgalazka.inz.serial.commands.*;
import pl.edu.pw.elka.sgalazka.inz.view.DataReceiver;

import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2015-11-15.
 */
public class CashRegisterCommandProcessor {

    private static BlockingQueue<String> toView;

    public static void setToViewQueue(BlockingQueue<String> blockingQueue) {
        toView = blockingQueue;
    }

    public static void runSaveDatabase(String portName) {
        Thread thread = new Thread(new SaveDatabaseCommand(toView, portName));
        thread.start();
    }

    public static void runDeleteDatabase(String portName) {
        Thread thread = new Thread(new DeleteDatabaseCommand(toView, portName));
        thread.start();
    }

    public static void runGetParsedData(DataReceiver dataReceiver, String portname) {
        Thread thread = new Thread(new GetPositionsCommand(toView, portname, dataReceiver));
        thread.start();
    }

    public static void runGetVatGroups(String portName) {
        Thread thread = new Thread(new GetVatGroupsCommand(toView, portName));
        thread.start();
    }
}
