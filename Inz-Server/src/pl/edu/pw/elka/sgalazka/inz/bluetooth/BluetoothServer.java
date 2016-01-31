package pl.edu.pw.elka.sgalazka.inz.bluetooth;

import pl.edu.pw.elka.sgalazka.inz.database.DatabaseManager;
import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.Log.LogType;
import pl.edu.pw.elka.sgalazka.inz.view.ProductsPanel;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ga��zka on 2015-10-08.
 */
public class BluetoothServer implements Runnable {

    private BlockingQueue<String> toScanner;
    private BlockingQueue<String> toClient;
    private BlockingQueue<String> toView;
    private BufferedReader in;
    private StreamConnectionNotifier streamConnNotifier;
    private RemoteDevice remoteDevice;
    private BluetoothClient client;
    private StreamConnection connection;
    private static boolean running;
    private final static Object lock = new Object();

    public BluetoothServer(BlockingQueue<String> toScanner, BlockingQueue<String> toView) {
        this.toScanner = toScanner;
        this.toView = toView;
        toClient = new LinkedBlockingQueue<>();
        running = true;
    }

    @Override
    public void run() {
        while (getRunning()) {
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            while (getRunning()) {
                try {
                    String lineRead = in.readLine();
                    Log.d("Serwer otrzymał: "+lineRead);
                    if (lineRead == null || lineRead.isEmpty() || lineRead.equals("null"))
                        break;

                    handleMessage(lineRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                streamConnNotifier.close();
                in.close();
                connection.close();
                client.setRunning(false);
                toClient.add(BluetoothClient.STOP_RUNNING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer() throws IOException {
        UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);

        String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

        streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        Log.d("Bluetooth:" + "Server Started. Waiting for clients to connect...");
        connection = streamConnNotifier.acceptAndOpen();

        remoteDevice = RemoteDevice.getRemoteDevice(connection);
        Log.d("Bluetooth:" + "Remote device address: " + remoteDevice.getBluetoothAddress());
        Log.d("Bluetooth:" + "Remote device name: " + remoteDevice.getFriendlyName(true));

        InputStream inStream = connection.openInputStream();
        in = new BufferedReader(new InputStreamReader(inStream));
    }

    private void handleMessage(String message) throws IOException {
        if (message.isEmpty() || message.equals("") || message.equals("null")) {
            return;
        }
        String splitted[] = message.split(":");
        if (message.charAt(0) == 'B') {
            toScanner.add(message);
        } else if (message.charAt(0) == 'D') {
            Log.d("to database: " + message);
            addToDatabase(message);

        } else if (message.equals("start")) {
            client = new BluetoothClient(toClient);

            client.runClient(remoteDevice);

            client.start();
            Log.d("Otrzymano wiadomosc startowa");
            toClient.add(message);
        } else if (message.equals("HeartBeat")) {
            toClient.add(message);
        } else if (splitted[0].equals("GPL")) {
            Log.d("sending list of products");
            sendListOfProducts(message);
        }
    }

    public static void setRunning(boolean val){
        synchronized (lock){
            running = val;
        }
    }

    private static boolean getRunning(){
        synchronized (lock){
            return running;
        }
    }

    private void addToDatabase(String msg){

        Runnable addToDatabaseCallback = new Runnable() {
            @Override
            public void run() {
                if (DatabaseManager.getInstance().addFromBluetooth(msg))
                    toView.add(ProductsPanel.DATA_CHANGED);
                else {
                    String splitted[] = msg.split(":");
                    toClient.add("EX:"+splitted[1]+":"+splitted[2]);
                }
            }
        };

        Thread thread = new Thread(addToDatabaseCallback);
        thread.start();
    }

    private void sendListOfProducts(String msg){
        Runnable sendListOfProductsCallback = new Runnable() {
            @Override
            public void run() {
                String splitted[] = msg.split(":");
                String tmp;
                if(splitted[1].equals("0")){
                    tmp = DatabaseManager.getInstance().getAllAsString();
                }
                else
                    tmp = DatabaseManager.getInstance().getByQuantityAsString(Integer.parseInt(splitted[1]));
                String toSend = new StringBuilder().append(splitted[0]).append(":").append(splitted[1])
                        .append(":").append(tmp).toString();
                System.out.println(toSend);
                toClient.add(toSend);
            }
        };
        Thread thread = new Thread(sendListOfProductsCallback);
        thread.start();
    }

}
