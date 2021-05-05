package com.example.ridesharingq.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.DistanceJson;
import com.example.ridesharingq.objects.OrderList;
import com.example.ridesharingq.objects.TipJson;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class TestActivity extends AppCompatActivity {
    Button login;
    TextView account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        login = findViewById(R.id.btn_);
        account = findViewById(R.id.test);
        login.setOnClickListener(v -> new Thread(() -> {
            try {
                getMatch("134");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start());

    }



    public void getMatch(String did){
        System.out.println("Matching");
        String getUrl = "http://"+ IPUtil.getIp()+":8080/DMatching/getOrderList";
        HashMap<String, String> map = new HashMap<>();
        map.put("id", did);
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
                JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
                System.out.println("buffer:" + buffer.toString());
                OrderList ol = JSONObject.toJavaObject(jsonObject, OrderList.class);
                is.close();
                reader.close();
                connection.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

