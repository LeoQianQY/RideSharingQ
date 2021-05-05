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
import java.util.HashMap;

public class DFinishActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfinish);
        final Intent intent = getIntent();
        String did = intent.getStringExtra("did");
        Button exit = findViewById(R.id.PExit);
        exit.setOnClickListener(v -> new Thread(() -> deleteQuery(did)).start());
        Button newQuery  = findViewById(R.id.newQuery);
        newQuery.setOnClickListener(v -> new Thread(() -> startNewQuery(did)).start());
    }

    public void deleteQuery(String did){
        System.out.println("deletQuerying");
        HashMap<String, String> map = new HashMap<>();
        map.put("did", did);
        String getUrl = "http://"+ IPUtil.getIp()+":8080/bindDriver/finishQuery";
        String reqJsonString = JSON.toJSONString(map);
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
                System.out.println(buffer.toString());
                is.close();
                reader.close();
                connection.disconnect();
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNewQuery(String did){
        System.out.println("startNewQuerying");
        HashMap<String, String> map = new HashMap<>();
        map.put("did", did);
        String getUrl = "http://"+IPUtil.getIp()+":8080/bindDriver/startNewQuery";
        String reqJsonString = JSON.toJSONString(map);
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
                System.out.println(buffer.toString());
                is.close();
                reader.close();
                connection.disconnect();
                Intent nextIntent = new Intent();
                nextIntent.setClass(DFinishActivity.this, DMatchingActivity.class);
                nextIntent.putExtra("did", did);
                this.startActivity(nextIntent);
                finish();
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
