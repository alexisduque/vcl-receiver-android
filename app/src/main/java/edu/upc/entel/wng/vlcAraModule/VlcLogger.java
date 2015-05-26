package edu.upc.entel.wng.vlcAraModule;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexis DUQUE - alexisd61@gmail.com.
 * Date : 25/05/15.
 */
public class VlcLogger {
    private Context context;

    public VlcLogger() {
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void saveRxBits(String rxBuffer) {
        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            saveRxBitsExternal(rxBuffer);
        } else {
            saveRxBitsInternal(rxBuffer);
        }
    }
    private void saveRxBitsExternal(String rxBuffer) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/VlcAraLogs/");
        myDir.mkdirs();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyy-HHmmss", Locale.FRANCE);
        String date = sdf.format(new Date());
        String filename = "record-"+ date +".txt";
        File file = new File (myDir, filename);
        if(file.exists())
        {
             writeToFile(file, rxBuffer);
        } else {
            try {
                file.createNewFile();
                writeToFile(file, rxBuffer);
            } catch (Exception e) {
                Log.e("VlcLogger", "Cant't create file" + file.getAbsolutePath());
            }
        }
    }

    private void writeToFile(File file, String string) {
        try {
            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(string);
            myOutWriter.close();
            fOut.close();
        } catch(Exception e) {
            Log.e("VlcLogger", "Cant't write to file" + file.getAbsolutePath());
        }
    }

    private void saveRxBitsInternal(String rxBuffer) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(context.getFilesDir() + "/VlcAraLogs/");
        myDir.mkdirs();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyy-HHmmss", Locale.FRANCE);
        String date = sdf.format(new Date());
        String filename = "record-"+ date +".txt";
        File file = new File (myDir, filename);
        if(file.exists())
        {
            writeToFile(file, rxBuffer);
        } else {
            try {
                file.createNewFile();
                writeToFile(file, rxBuffer);
            } catch (Exception e) {
                Log.e("VlcLogger", "Cant't create file" + file.getAbsolutePath());
            }
        }

    }


}
