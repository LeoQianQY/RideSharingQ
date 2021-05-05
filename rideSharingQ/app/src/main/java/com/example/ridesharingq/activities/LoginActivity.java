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
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Account;
import com.example.ridesharingq.objects.OngoingAccount;
import com.example.ridesharingq.objects.Query;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.login_button);
        Button cancel = findViewById(R.id.cancelLogin_button);
        cancel.setOnClickListener(v -> {
            this.finish();
        });
        login.setOnClickListener(v -> {
            new Thread(() -> login()).start();
        });
    }

    public void login(){
        System.out.println("Logging");
        EditText id = findViewById(R.id.ID);
        EditText password = findViewById(R.id.password);
        Account ac = new Account();
        ac.setId(id.getText().toString());
        ac.setPassword(password.getText().toString());
        System.out.println("password: " +ac.getId());
        String getUrl = "http://"+ IPUtil.getIp()+":8080/Account/login";
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
                JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
                System.out.println("buffer:" + buffer.toString());
                OngoingAccount o = JSONObject.toJavaObject(jsonObject, OngoingAccount.class);
                is.close();
                reader.close();
                connection.disconnect();
                if(o.getResult() == 1){
                    Intent intent = new Intent();
                    if(o.getDid()==null && o.getPid() == null) {
                        intent.setClass(LoginActivity.this, RoleActivity.class);
                        intent.putExtra("id", id.getText().toString());
                    }else if(o.getDid() != null){
                        if(o.getState() == 0){
                            intent.setClass(LoginActivity.this, DMatchingActivity.class);
                            intent.putExtra("did", o.getDid());
                        }else if(o.getState() == 1){
                            intent.setClass(LoginActivity.this, BindDriverActivity.class);
                            intent.putExtra("pid", o.getPid());
                            intent.putExtra("pickup", o.getPickupName());
                            intent.putExtra("dropoff", o.getDropoffName());
                            intent.putExtra("lst", o.getBindDriverLst());
                        }
                        else if(o.getState() == 2){
                            intent.setClass(LoginActivity.this, BindDriver2Activity.class);
                            intent.putExtra("did", o.getDid());
                            intent.putExtra("dropoff", o.getDropoffName());
                            intent.putExtra("lst", o.getBindDriverLst());
                        }else if(o.getState() == 3){
                            intent.setClass(LoginActivity.this, DFinishActivity.class);
                            intent.putExtra("did", o.getDid());
                        }
                    }else{
                        if(o.getState() == 0){
                            intent.setClass(LoginActivity.this, PMatchingActivity.class);
                            intent.putExtra("pid", o.getPid());
                            intent.putExtra("timeExpected", Long.valueOf(o.getTimeExpected()));
                        }else if(o.getState() == 1){
                            intent.setClass(LoginActivity.this, BindPassengerActivity.class);
                            intent.putExtra("pid", o.getPid());
                            intent.putExtra("pickup", o.getBindDriverLst().get(0));
                            intent.putExtra("dropoff", o.getBindDriverLst().get(1));
                            intent.putExtra("driveLoc", o.getBindDriverLst().get(2));
                        }
                        else if(o.getState() == 2){
                            intent.setClass(LoginActivity.this, BindPassenger2Activity.class);
                            intent.putExtra("pid", o.getPid());
                            intent.putExtra("pickup", o.getBindDriverLst().get(0));
                            intent.putExtra("dropoff", o.getBindDriverLst().get(1));
                        }else if(o.getState() == 3){
                            intent.setClass(LoginActivity.this, PFinishActivity.class);
                            intent.putExtra("pid", o.getPid());
                        }
                    }
                    this.startActivity(intent);
                    finish();
                }else{
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "Invalid ID or Password!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
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
