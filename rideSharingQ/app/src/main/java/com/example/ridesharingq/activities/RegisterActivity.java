package com.example.ridesharingq.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Account;
import com.example.ridesharingq.util.IPUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button confirm = findViewById(R.id.confirm_button);
        Button cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(v -> {
            this.finish();
        });
        confirm.setOnClickListener(v -> {
            new Thread(this::register).start();
        });
    }

    public void register(){
        System.out.println("register");
        EditText id = findViewById(R.id.accountId);
        EditText name = findViewById(R.id.accountName);
        EditText password = findViewById(R.id.editTextNumberPassword);
        Account ac = new Account();
        ac.setId(id.getText().toString());
        ac.setName(name.getText().toString());
        ac.setPassword(password.getText().toString());
        System.out.println(ac.getId());
        String getUrl = "http://"+ IPUtil.getIp()+":8080/Account/register";
        String reqJsonString = JSON.toJSONString(ac);
        try{
            URL url = new URL(getUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(reqJsonString);
            connection.getOutputStream().write(reqJsonString.getBytes());
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String readLine = "";
                while ((readLine = reader.readLine()) != null) {
                    buffer.append(readLine);
                }
                if(Integer.parseInt(buffer.toString()) == 0){
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, "The account has been registered.",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                is.close();
                reader.close();
                connection.disconnect();
            }
            Intent nextIntent = new Intent();
            nextIntent.setClass(RegisterActivity.this, RoleActivity.class);
            nextIntent.putExtra("id", id.getText().toString());
            this.startActivity(nextIntent);
            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
