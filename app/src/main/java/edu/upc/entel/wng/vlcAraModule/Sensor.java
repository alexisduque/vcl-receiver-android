/**
 * Created by Alexis DUQUE - alexisd61@gmail.com.
 * Date : 10/04/15.
 */

package edu.upc.entel.wng.vlcAraModule;

import android.content.Context;
import android.hardware.I2cManager;
import android.hardware.I2cTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sensor {
    // The 7-bit slave address
    private static final int address = (0x50 >> 1);
    private static final String bus = "/dev/i2c-4";

    private static final int BUFFER_SIZE = 50;
    private static final String TAG = "AraVLC";
    private static final String BUF = "RxData@";
    private Context context;
    private I2cManager i2c;
    private Handler handler;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static I2cTransaction WriteReg(int reg, int b1, int b2, int b3) {
        return I2cTransaction.newWrite(0x01, reg, b1, b2, b3);
    }

    private static final I2cTransaction[] bufferRead = {
        I2cTransaction.newRead(BUFFER_SIZE),
    };

    private byte[] getRandom() {
        byte[] result = new byte[50];
        byte[] values = {15, 23, 27, 29, 30, 39, 43, 45, 46, 47, 51, 53, 54, 57, 58, 60};
        for (int i = 0; i < 50; i++) {
            result[i] = values[(new Random().nextInt(values.length - 1))];
        }
       return result;
    }

    public Sensor(Context context, Handler handler) {
        this.context = context;
        this.i2c = (I2cManager)context.getSystemService("i2c");
        this.handler = handler;
    }

    private I2cTransaction[] execute(I2cTransaction[] txns) {
        I2cTransaction[] results;
        try {
            results = null;
            for (I2cTransaction txn: txns)
                results = i2c.performTransactions(bus, address, txn);
        } catch (IOException e) {
            throw new RuntimeException("I2C error: " + e);
        }
        return results;
    }

    public void start() {
        executor.scheduleAtFixedRate(collector, 30, 30, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            assert false;
        }
    }


    private final Runnable collector = new Runnable() {
        public void run() {
            I2cTransaction[] results;
            byte[] data;
            int[] val;
            results = execute(bufferRead);
            data = results[0].data;
            //data = getRandom();
            logBinary(data);
        }
    };

    private void logBinary(byte[] data) {
        String binary;
        Message m = new Message();
        Date rxTimestamp = new Date();
        StringBuilder sbBinary = new StringBuilder("");
        for (byte b1 : data) {
            String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
            sbBinary.append(s1.substring(1));
            sbBinary.append(" ");
        }
        binary = sbBinary.toString();
        Bundle b = new Bundle();
        b.putString("rxString", binary);
        m.setData(b);
        handler.sendMessage(m);
        Log.d(BUF + rxTimestamp.getTime() + ':', binary);
    }

    private int[] getBinary(byte[] bytesArray) {
        int bits[] = new int[BUFFER_SIZE];
        int i = 0;
        for (byte b : bytesArray) {
            String binary = Integer.toBinaryString(b & 255 | 256).substring(1);
            bits[i] = Integer.parseInt(binary);
            i++;
        }
        return bits;
    }

}
