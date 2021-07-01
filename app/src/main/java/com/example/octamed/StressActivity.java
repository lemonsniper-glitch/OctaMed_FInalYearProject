package com.example.octamed;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class StressActivity extends AppCompatActivity {

    ImageView heart_icon;
    ImageView bpmheart;
    Button runbtn;
    TextView timer;
    TextView bpmtext;
    TextView stressText;
    CountDownTimer countDownTimer;
    long timeLeftInMilliSeconds = 30000;
    boolean timerRunning;
    int average = 0;
    int bpmavgcounter=0;
    boolean running = false;
    boolean running2 = false;


    int pointsplotted = 5;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    int point = 5;
    Viewport viewport;
    BluetoothSocket btsocket;
    BluetoothSocket btsocket2;
    OutputStream outputStream;
    OutputStream outputStream2;
    Button plot;
    Button stop;
    Handler hand = new Handler();
    Handler hand2 = new Handler();
    InputStream inputStream ;
    InputStream inputStream2 ;
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 512),
            new DataPoint(1, 512),
            new DataPoint(2, 312),
            new DataPoint(3, 202),
            new DataPoint(4, 506),

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress);

        heart_icon = findViewById(R.id.heart_icon);
        heart_icon.setAlpha(95);

        bpmheart = findViewById(R.id.bpmheart);
        bpmheart.setAlpha(95);

        bpmtext = findViewById(R.id.bpmtext);



        timer = findViewById(R.id.timer);

        stressText = findViewById(R.id.stress_text);

        hand = new Handler();
        hand2 = new Handler();

        GraphView graph = (GraphView) findViewById(R.id.graph);

        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsVAlign(GridLabelRenderer.VerticalLabelsVAlign.ABOVE);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        viewport.setMaxX(500);
        viewport.setMinX(0);
        viewport.setMinY(100);
        viewport.setMaxY(512);
        series.setTitle("GSR Reading");
        series.setColor(Color.BLACK);
        //series.setDrawDataPoints(true);
        series.setColor(Color.parseColor("#941e94"));
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setTextSize(25);
        graph.getLegendRenderer().setBackgroundColor(Color.argb(160, 50, 0, 0));
        graph.getLegendRenderer().setTextColor(Color.WHITE);
        series.setThickness(2);
        graph.addSeries(series);

    }

    public void plot(View view){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.i("prm", String.valueOf(bluetoothAdapter.getBondedDevices()));
        Log.i("prm",Integer.toString(2));
        BluetoothDevice hc = bluetoothAdapter.getRemoteDevice("00:21:13:01:F8:8C");
        Log.i("prm",hc.getName());
        btsocket= null;
        int count =0;
        do {
            count++;
            try {
                btsocket = hc.createRfcommSocketToServiceRecord(mUUID);
                Log.i("prm", String.valueOf(btsocket));
                btsocket.connect();
                Log.i("prm", String.valueOf(btsocket.isConnected()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("prm", String.valueOf(btsocket.isConnected()));
            }
        }while (!btsocket.isConnected() && count<10);

        try {
            outputStream = btsocket.getOutputStream();
            outputStream.write(49);
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            inputStream = btsocket.getInputStream();
            inputStream.skip(inputStream.available());
            Log.i("prm","Reached");
            int hold = 0;
            running = true;
            hand.postDelayed(plotg,500);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable plotg = new Runnable() {
        @Override
        public void run() {

            int a = 0;
            int b = 0;
            if (running) {
                try {

                    a = (inputStream.read());
                    b = (inputStream.read());
                    int ans = (a * 256) + b;
                    Log.i("prm", "Value of GSR: " + String.valueOf((a * 256) + b));
                    pointsplotted++;
                    if (ans < 100) {
                        series.setColor(Color.parseColor("#f70000"));
                        stressText.setTextColor(Color.RED);
                        stressText.setText("Hyper stress");
                    } else if (ans > 100 && ans < 260) {
                        series.setColor(Color.parseColor("#f73600"));
                        stressText.setTextColor(Color.rgb(247, 54, 0));
                        stressText.setText("High stress");
                    } else if (ans > 280 && ans < 345) {
                        series.setColor(Color.parseColor("#0008f7"));
                        stressText.setTextColor(Color.rgb(0, 8, 247));
                        stressText.setText("Medium stress");
                    } else if (ans > 345) {
                        series.setColor(Color.parseColor("#00f704"));
                        stressText.setTextColor(Color.rgb(0, 247, 4));
                        stressText.setText("No stress");
                    }
                    series.appendData(new DataPoint(point++, (a * 256) + b), true, pointsplotted);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                hand.postDelayed(this, 1000);
            }
        }
    };

    public void stop(View view){

        try {
            outputStream.write(52);
            running =false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            btsocket.close();
            Log.i("prm","Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bpmMonitorStart(View view){
        startTimer();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.i("prm", String.valueOf(bluetoothAdapter.getBondedDevices()));
        Log.i("prm",Integer.toString(2));
        BluetoothDevice hc = bluetoothAdapter.getRemoteDevice("00:21:13:01:F8:8C");
        Log.i("prm",hc.getName());
        btsocket= null;
        int count =0;
        do {
            count++;
            try {
                btsocket = hc.createRfcommSocketToServiceRecord(mUUID);
                Log.i("prm", String.valueOf(btsocket));
                btsocket.connect();
                Log.i("prm", String.valueOf(btsocket.isConnected()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("prm", String.valueOf(btsocket.isConnected()));
            }
        }while (!btsocket.isConnected() && count<10);

        try {
            outputStream = btsocket.getOutputStream();
            int i=0;
            outputStream.write(50);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream = btsocket.getInputStream();
            inputStream.skip(inputStream.available());
            Log.i("prm","Reached");
            int hold = 0;
            running2 = true;
            hand.postDelayed(bpm,500);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bpmMonitorStop(View view){
        startTimer();
        try {
            outputStream.write(52);
            running2 = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bpmtext.setText(String.valueOf(average/bpmavgcounter));
            average=0;
            btsocket.close();
            Log.i("prm","Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Runnable bpm = new Runnable() {
        @Override
        public void run() {
            if (running2) {
                int a = 0;
                int b = 0;
                try {
                    a = (inputStream.read());
                    //b = (inputStream.read());
                    Log.i("prm", "The value of a: " + String.valueOf(a));
                    bpmtext.setText(String.valueOf(a));
                    average = average + a;
                    bpmavgcounter++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hand.postDelayed(this, 0);
            }
        }
    };

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliSeconds,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliSeconds = millisUntilFinished;
                timer.setText(Long.toString(timeLeftInMilliSeconds/1000));
            }

            @Override
            public void onFinish() {
                timer.setText("Done");
            }
        }.start();
    }

    private void stopTimer() {
        countDownTimer.cancel();
    }

    public void returnHome(View view){
        startActivity(new Intent(this,HomeActivity.class));
    }


}

//                else{
//                    int Min = 90;
//                    int Max = 110;
//                    corrector = Min + (int)(Math.random() * ((Max - Min) + 1));
//                    bpmtext.setText(String.valueOf((int)corrector));
//                    average = average+(int)corrector;
//                    bpmavgcounter++;
//                }