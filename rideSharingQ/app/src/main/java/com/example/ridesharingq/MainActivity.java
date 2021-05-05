package com.example.ridesharingq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.ridesharingq.activities.LoginActivity;
import com.example.ridesharingq.activities.MapActivity;
import com.example.ridesharingq.activities.RegisterActivity;
import com.example.ridesharingq.activities.TestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt = findViewById(R.id.exit);
        bt.setOnClickListener(v -> {
            System.exit(0);
        });
        Button register = findViewById(R.id.registerBt);
        register.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        Button login = findViewById(R.id.loginBt);
        login.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}