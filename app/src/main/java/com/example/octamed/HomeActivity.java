package com.example.octamed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    TextView homename, presentDate, homeday;
    SharedPreferences sharedPreferences;
    String passedName;
    ImageView hear_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Setting up User Name
        homename = findViewById(R.id.homename);
        sharedPreferences  =  this.getSharedPreferences("com.example.octamed",Context.MODE_PRIVATE);
        String sName = sharedPreferences.getString("Name","Error");
        if(sName.equals("Error")) {
            setName();
        }
        homename.setText("Hi, "+sharedPreferences.getString("Name",""));

        //Setting Current date
        presentDate = findViewById(R.id.homedate);
        homeday = findViewById(R.id.homeday);
        presentDate.setText(giveDate());
        homeday.setText(giveDay());

        //heart
        hear_img = findViewById(R.id.heart_img);
        hear_img.setAlpha(95);

    }

    public void setName(){
        passedName = getIntent().getStringExtra("UserName");
        sharedPreferences.edit().putString("Name",passedName).commit();
        homename.setText(sharedPreferences.getString("Name",""));
    }

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        return sdf.format(cal.getTime());
    }
    public String giveDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(cal.getTime());
    }

    public void stress(View view){
        startActivity(new Intent(this,StressActivity.class));
    }

    public void ulcer(View view){
        startActivity(new Intent(this,PressureUlcerActivity.class));
    }

    public void gotochat(View view){
        startActivity(new Intent(this,HomeChatActivity.class));
    }

    public void alter(View view){
        startActivity(new Intent(this,RelaxActivity.class));
    }

    public void calender(View view){
        startActivity(new Intent(this,Calender.class));
    }
}