package pl.edu.pw.elka.sgalazka.inz.serial.commands;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.serial.DLLFunctions;

import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2016-02-12.
 */
public class DeleteDatabaseCommand extends CashRegisterCommand {
    public final static String NOTIFY_DELETE= "notifyDelete";
    public final static String DELETE_ERROR = "deleteError";

    public DeleteDatabaseCommand(BlockingQueue<String> queue, String portName) {
        super(queue, portName);
    }

    @Override
    void process() {
        toView.add(CashRegisterCommand.WAIT);
        modifyConfigFile(portName);
        Log.d("Zmodyfikowano plik KONFIG");
        modifyInputFile();
        try{
            int result = DLLFunctions.deleteWareDatabase();
            if (result!=0){
                toView.add(DELETE_ERROR);
                return;
            }
        }
        catch (UnsatisfiedLinkError e){
            toView.add(CashRegisterCommand.NO_DLL_ERROR);
        }
        Log.d("Pomyślnie usunięto bazę towarów na kasie");
        toView.add(NOTIFY_DELETE);
    }
}
