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

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.clcworld.araplox.R;

public class AraPloxActivity extends Activity {
    private Chart irChart, redChart;
    private Sensor sensor;

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            irChart.add((int[])msg.obj);
            //redChart.add(msg.arg2);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ara_plox);
        
        TextView irTextView = (TextView) findViewById(R.id.irTextView);
        irTextView.setTextColor(Color.WHITE);
        
        TextView redTextView = (TextView) findViewById(R.id.redTextView);
        redTextView.setTextColor(Color.WHITE);


        irChart = new Chart(this, (LinearLayout) findViewById(R.id.irChart), "ir", Color.CYAN);
        redChart = new Chart(this, (LinearLayout) findViewById(R.id.redChart), "red", Color.RED);
    }

    @Override
    protected void onResume() {
        irChart.clear();
        redChart.clear();
        super.onResume();
        sensor = new Sensor(this, handler);
        sensor.start();
    }

    @Override
    protected void onPause() {
        sensor.stop();
        sensor = null;
        handler.removeMessages(0);
        super.onPause();
    }
}
