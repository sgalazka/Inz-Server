package pl.edu.pw.elka.sgalazka.inz.server.bluetooth;

import pl.edu.pw.elka.sgalazka.inz.server.Log.Log;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * Created by gałązka on 2015-12-30.
 */
public class BluetoothClient extends Thread implements DiscoveryListener {

    public final static String STOP_RUNNING = "stopRunning";
    // object used for waiting
    private static final Object lock = new Object();
    private final Object runLock = new Object();

    private PrintWriter pWriter;
    // vector containing the devices discovered
    private static Vector<RemoteDevice> vecDevices = new Vector<RemoteDevice>();

    // device connection address
    private static String connectionURL = null;
    private static BlockingQueue<String> queue;

    private StreamConnection streamConnection;
    private OutputStream outStream;

    private boolean running;

    public BluetoothClient(BlockingQueue queue) {
        this.queue = queue;
    }

    /**
     * runs a bluetooth client that sends a string to a server and prints the response
     */
    public void runClient(RemoteDevice remoteDevice) throws IOException {
        Log.i("starting client");

        setRunning(true);

        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());

        DiscoveryAgent agent = localDevice.getDiscoveryAgent();

        UUID[] uuidSet = new UUID[1];

        uuidSet[0] = new UUID("446118f08b1e11e29e960800200c9a66", false);

        System.out.println("\nSearching for services...");
        agent.searchServices(null, uuidSet, remoteDevice, this);

        // avoid callback conflicts
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // check
        if (connectionURL == null) {
            System.out.println("Device does not support Service.");
            System.exit(0);
        }

        // connect to the server

        try {
            streamConnection = (StreamConnection) Connector.open(connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Connected to server.");

        // send string
        outStream = streamConnection.openOutputStream();
        pWriter = new PrintWriter(new OutputStreamWriter(outStream));
        //agent.cancelServiceSearch();

    }

    // methods of DiscoveryListener
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        // add the device to the vector
        if (!vecDevices.contains(btDevice)) {
            vecDevices.addElement(btDevice);
        }
    }

    // implement this method since services are not being discovered
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        if (servRecord != null && servRecord.length > 0) {
            connectionURL = servRecord[0].getConnectionURL(0, false);
        }
        synchronized (lock) {
            lock.notify();
        }
    }

    // implement this method since services are not being discovered
    public void serviceSearchCompleted(int transID, int respCode) {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void inquiryCompleted(int discType) {
        synchronized (lock) {
            lock.notify();
        }

    }// end method

    public void sendMessage(String msg) {
        //Log.i("Bt client sent: " + msg);
        pWriter.write(msg + "\r\n");
        pWriter.flush();
    }

    @Override
    public void run() {
        //super.run();
        while (isRunning()) {
            try {
                String tmp = queue.take();
                if(tmp.equals(STOP_RUNNING))
                    break;
                sendMessage(tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        pWriter.close();
        try {
            streamConnection.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Client stops running");
        System.out.println("Client stops running");
    }

    public static void addToSendQueue(String message) {
        queue.add(message);
    }

    public void setRunning(boolean val){
        synchronized (runLock){
            running = val;
        }
    }

    public boolean isRunning(){
        synchronized (runLock){
            return running;
        }
    }
}
