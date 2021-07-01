package com.example.octamed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BasicActivity extends AppCompatActivity {

    EditText name, age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
    }

    public void moveToHome(View view){

        String userName = name.getText().toString();
        String userAge = age.getText().toString();
        Intent intent = new Intent(BasicActivity.this,HomeActivity.class);
        intent.putExtra("UserName",userName);
        intent.putExtra("UserAge",userAge);
        startActivity(intent);

    }

}