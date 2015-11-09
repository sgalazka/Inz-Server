package pl.edu.pw.elka.sgalazka.inz.serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import pl.edu.pw.elka.sgalazka.inz.view.Log;

import java.util.concurrent.BlockingQueue;

/**
 * Created by ga³¹zka on 2015-10-17.
 */
public class BarcodeScanner implements Runnable {

    private BlockingQueue<String> fromBluetooth;
    private SerialPort serialPort;

    public BarcodeScanner(BlockingQueue<String> queue) {
        fromBluetooth = queue;

        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        serialPort = new SerialPort("COM11");

        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_EVEN);
        } catch (SerialPortException ex) {
            System.out.println(ex);
            Log.e("SCANNER: SerialPortException on open");
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!fromBluetooth.isEmpty()) {
                try {
                    handleMessage(fromBluetooth.take());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleMessage(String message) {
        String args[] = message.split(":");
        String serialData = args[1] + 0x0D;
        Log.d("SCANNER: " + args[1] + " " + args[2]);
        try {
            serialPort.writeBytes(serialData.getBytes());
        } catch (SerialPortException ex) {
            System.out.println(ex);
            Log.e("SCANNER: SerialPortException on send");
        }
    }
}
