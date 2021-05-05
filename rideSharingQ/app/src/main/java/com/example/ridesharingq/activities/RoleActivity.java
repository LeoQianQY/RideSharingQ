package com.example.ridesharingq.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Account;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        final Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Button logout = findViewById(R.id.logout);
        Button driver = findViewById(R.id.driver);
        Button passenger = findViewById(R.id.passenger);
        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("yes", (dialog, which) -> finish());
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(RoleActivity.this, "logout failed",Toast.LENGTH_SHORT).show());
            builder.show();
        });
        driver.setOnClickListener(vi -> {
            Intent newIntent = new Intent();
            newIntent.setClass(RoleActivity.this, DriverActivity.class);
            newIntent.putExtra("id", id);
            this.startActivity(newIntent);
        });
        passenger.setOnClickListener(vw -> {
            Intent newIntent2 = new Intent();
            newIntent2.setClass(RoleActivity.this, PassengerActivity.class);
            newIntent2.putExtra("id", id);
            this.startActivity(newIntent2);
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
