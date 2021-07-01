package com.example.octamed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class foot extends AppCompatActivity {

    CardView bgc;
    boolean running = false;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Viewport viewport;
    BluetoothSocket btsocket;
    OutputStream outputStream;
    Button monitor;
    Button stop;
    Handler hand = new Handler();
    InputStream inputStream ;
    TextView skintxt;
    LinearLayout llbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foot);

        bgc = findViewById(R.id.colourcard);
        monitor = findViewById(R.id.mon1);
        stop = findViewById(R.id.stop1);
        skintxt = findViewById(R.id.skinres_text);
        llbg = findViewById(R.id.llbg);

        hand = new Handler();
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
            outputStream.write(51);
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
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {

            int a = 0;
            int b = 0;
            long humanres = 0;
            if (running) {
                try {

                    a = (inputStream.read());
                    b = (inputStream.read());
                    int ans = (a * 256) + b;
                    Log.i("prm", "Value of GSR: " + String.valueOf((a * 256) + b));
                    humanres= ((1024+2*ans)*10000)/(1000-ans);
                    Log.i("prm","Human Resistance: "+String.valueOf(humanres));
                    if(humanres < 12000){
                        skintxt.setText(String.valueOf(humanres)+" ohms");
                        llbg.setBackgroundColor(Color.rgb(255,0,0));
                    }else {
                        skintxt.setText(String.valueOf(humanres)+" ohms");
                        llbg.setBackgroundColor(Color.rgb(21,255,0));
                    }



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
}
//