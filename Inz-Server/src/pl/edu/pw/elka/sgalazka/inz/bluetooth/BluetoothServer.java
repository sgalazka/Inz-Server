package pl.edu.pw.elka.sgalazka.inz.bluetooth;

import pl.edu.pw.elka.sgalazka.inz.view.Log;
import pl.edu.pw.elka.sgalazka.inz.view.LogType;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ga³¹zka on 2015-10-08.
 */
public class BluetoothServer implements Runnable {

    private BlockingQueue<LogType> queue;
    private BlockingQueue<String> toScanner;
    private BufferedReader in;
    private StreamConnectionNotifier streamConnNotifier;
    public BluetoothServer(BlockingQueue<LogType> queue, BlockingQueue<String> toScanner) {
        this.queue = queue;
        this.toScanner = toScanner;
    }
    @Override
    public void run() {
        while(true){
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true){
                try {
                    String lineRead = in.readLine();
                    if(lineRead == null || lineRead.isEmpty() || lineRead.equals("null"))
                        break;
                    Log.d("Bluetooth:" + lineRead);
                    handleMessage(lineRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void startServer() throws IOException{
        UUID uuid = new UUID("0000110100001000800000805f9b34fb",false);
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";

        streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        Log.d("Bluetooth:" + "Server Started. Waiting for clients to connect...");
        StreamConnection connection=streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        Log.d("Bluetooth:" + "Remote device address: " + dev.getBluetoothAddress());
        Log.d("Bluetooth:" + "Remote device name: " + dev.getFriendlyName(true));
        InputStream inStream=connection.openInputStream();
        in = new BufferedReader(new InputStreamReader(inStream));

    }
    private void handleMessage(String message){
        if(message.isEmpty() || message.equals("") || message.equals("null")){
            return;
        }
        if(message.charAt(0) == 'B'){
            toScanner.add(message);
        }
        else if(message.charAt(0) == 'D'){
            //wyslij do bazy danych
        }
    }
}
