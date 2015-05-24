/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.araploxio;

import android.content.Context;
import android.hardware.I2cManager;
import android.hardware.I2cTransaction;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sensor {
    // The 7-bit slave address
    private static final int address = (0x50 >> 1);
    private static final String bus = "/dev/i2c-4";

    private static final int BUFFER_SIZE = 150;
    private static final String TAG = "AraVLC";
    private static final String BUF = "RxData@";

    private Context context;
    private I2cManager i2c;
    private Handler handler;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static I2cTransaction WriteReg(int reg, int b1, int b2, int b3) {
        return I2cTransaction.newWrite(0x01, reg, b1, b2, b3);
    }

    private static final I2cTransaction[] setupTxns = {

    };

    private static final I2cTransaction[] bufferRead = {
        I2cTransaction.newRead(BUFFER_SIZE),
    };

    private static final I2cTransaction[] resetTxns = {
        //WriteReg(0x00, 0x00, 0x00, 0x08),
    };

    public Sensor(Context context, Handler handler) {
        this.context = context;
        //noinspection ResourceType
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
        //execute(setupTxns);
        executor.scheduleAtFixedRate(collector, 20, 20, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            assert false;
        }
        execute(resetTxns);
    }

    private final Runnable collector = new Runnable() {
        public void run() {
            I2cTransaction[] results;
            byte[] data;
            int[] val;
            results = execute(bufferRead);
            data = results[0].data;
            //val = getBinary(data);
            logBinary(data);
            //handler.obtainMessage(0, val).sendToTarget();
        }
    };

    private void logBinary(byte[] data) {
        String binary;
        Date rxTimestamp = new Date();
        StringBuilder sbBinary = new StringBuilder("");
        for (byte b1 : data) {
            String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
            sbBinary.append(s1);
        }
        binary = sbBinary.toString();
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
