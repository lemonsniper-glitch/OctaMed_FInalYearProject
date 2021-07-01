package com.example.octamed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RelaxActivity extends AppCompatActivity {

    Button yt_btn;
    Button sp_btn;
    Button ph_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relax);

        yt_btn = findViewById(R.id.btnforyt);
        sp_btn = findViewById(R.id.btnforspotify);
        ph_btn = findViewById(R.id.btnforph);
    }

    public void gotoyt(View view){
        startActivity(new Intent(this,Youtube.class));
    }

    public void gotospotify(View view){
        startActivity(new Intent(this,Spotify.class));
    }

    public void gotoph(View view){
        startActivity(new Intent(this,PH.class));
    }

    public void home(View view){
        startActivity(new Intent(this,HomeActivity.class));
    }
}