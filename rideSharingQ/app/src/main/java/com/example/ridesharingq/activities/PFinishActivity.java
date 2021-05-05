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

public class PFinishActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfinish);
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        new Thread(() -> deleteOrder(pid)).start();
        Button exit = findViewById(R.id.PExit);
        exit.setOnClickListener(v -> new Thread(() -> closeApp()).start());
    }

    public void deleteOrder(String pid){
        System.out.println("deletOrdering");
        HashMap<String, String> map = new HashMap<>();
        map.put("pid", pid);
        String getUrl = "http://"+ IPUtil.getIp()+":8080/PMatching/finishOrder";
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

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeApp(){
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
