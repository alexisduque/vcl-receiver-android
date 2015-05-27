/**
 * Created by Alexis DUQUE - alexisd61@gmail.com.
 * Date : 10/04/15.
 */

package edu.upc.entel.wng.vlcAraModule;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class VlcAraActivity extends Activity {
    private Chart redChart;
    private Sensor sensor;
    private EditText rxText;
    private Button clearBtn;
    private Button saveBtn;
    private Switch enLog;
    private VlcLogger vlcLogger;

    private final Handler rxHandler = new Handler() {
        public void handleMessage(Message msg) {
            String rxBits = msg.getData().getString("rxString");
            if (rxText.getText().length() > 1000) clearEditText();
            if (enLog.isChecked()) vlcLogger.saveRxBits(rxBits);
            rxText.append(rxBits + System.getProperty("line.separator"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ara_plox);
        
        TextView irTextView = (TextView) findViewById(R.id.irTextView);
        irTextView.setTextColor(Color.WHITE);

        rxText = (EditText) findViewById(R.id.rxText);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
                                           saveEditText();
                                       }
                                   });

        clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearEditText();
            }
        });

        vlcLogger = new VlcLogger();

        enLog = (Switch) findViewById(R.id.switch1);

        rxText.append(System.getProperty("line.separator") + "App is started !!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensor = new Sensor(this, rxHandler);
        sensor.start();
    }

    @Override
    protected void onPause() {
        sensor.stop();
        sensor = null;
        rxHandler.removeMessages(0);
            super.onPause();
    }

    private void clearEditText() {
        rxText.setText("");
    }

    private void saveEditText() {
        vlcLogger.saveRxBits(rxText.getText().toString());
    }
}
