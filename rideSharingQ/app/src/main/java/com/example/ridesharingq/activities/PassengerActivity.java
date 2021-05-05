package com.example.ridesharingq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Order;
import com.example.ridesharingq.objects.Query;
import com.example.ridesharingq.objects.Tip;
import com.example.ridesharingq.objects.TipJson;
import com.example.ridesharingq.runnables.TipRunnable;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class PassengerActivity extends AppCompatActivity {
    final String beijing_loc = "010";
    private String startName = "";
    private String endName = "";
    private HashMap<String, Tip> startMap = new HashMap<>();
    private HashMap<String, Tip> endMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("id");
        EditText et = findViewById(R.id.timeWindow);
        AutoCompleteTextView autoText = findViewById(R.id.autoStart);
        AutoCompleteTextView autoDes = findViewById(R.id.autoDest);
        autoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    startName = "";
                    return;
                }
                String content = s.toString().trim();
                TipRunnable task = new TipRunnable(content);
                Thread tipThread = new Thread(task);
                tipThread.start();
                try {
                    tipThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tip[] result = task.getResult();
                int len = result.length;
                String[] resultLoc = new String[len - 1];
                for(int i = 0;i<len-1;i++){
                    System.out.println("resultloc:  "+result[i].getName());
                    resultLoc[i] = result[i].getName();
                    startMap.put(resultLoc[i], result[i]);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PassengerActivity.this, android.R.layout.simple_list_item_1, resultLoc);
                autoText.setAdapter(arrayAdapter);
                autoText.setThreshold(1);
                autoText.setOnItemClickListener((parent, view, position, id1) -> {
                    startName = autoText.getText().toString();
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    endName = "";
                    return;
                }
                String content = s.toString().trim();
                TipRunnable task = new TipRunnable(content);
                Thread tipThread = new Thread(task);
                tipThread.start();
                try {
                    tipThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tip[] result = task.getResult();
                int len = result.length;
                String[] resultLoc = new String[len - 1];
                for(int i = 0;i<len-1;i++){
                    System.out.println("resultloc:  "+result[i].getName());
                    resultLoc[i] = result[i].getName();
                    endMap.put(resultLoc[i], result[i]);
                }
                System.out.println("startset:   " + endMap.entrySet());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PassengerActivity.this, android.R.layout.simple_list_item_1, resultLoc);
                autoDes.setAdapter(arrayAdapter);
                autoDes.setThreshold(1);
                autoDes.setOnItemClickListener((parent, view, position, id1) -> {
                    endName = autoDes.getText().toString();
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button confirm = findViewById(R.id.confirmOrder);
        Button cancel = findViewById(R.id.cancelOrder);
        cancel.setOnClickListener(v -> {
            this.finish();
        });
        confirm.setOnClickListener(v -> {
            String timeWindow = et.getText().toString();
            if(timeWindow.equals("")){
                Toast.makeText(PassengerActivity.this, "Please enter your expected time", Toast.LENGTH_SHORT).show();
                return;
            }
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
            long plus = (long)Integer.parseInt(timeWindow)*60000;
            long t = System.currentTimeMillis();
            long timeExpected = plus+t;
            String dateStr = String.valueOf(t+plus);
            System.out.println("this is the startMap*****:    "+startMap.entrySet());
            System.out.println("this is the endMap*****:    "+endMap.entrySet());
            System.out.println(startName);
            System.out.println(endName);
            if(!startMap.containsKey(startName) || ! endMap.containsKey(endName)){
                Toast.makeText(PassengerActivity.this, "Invalid startLocation or destination!", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> startOrder(pid,  startMap.get(startName).getLocation(), endMap.get(endName).getLocation(), dateStr,
                    startName, endName, startMap.get(startName).getAddress()
                    , endMap.get(endName).getAddress())).start();
            Intent nextIntent = new Intent();
            nextIntent.setClass(PassengerActivity.this, PMatchingActivity.class);
            nextIntent.putExtra("pid", pid);
            nextIntent.putExtra("timeExpected", timeExpected);
            nextIntent.putExtra("pickup", startMap.get(startName).getLocation());
            nextIntent.putExtra("dropoff", endMap.get(endName).getLocation());
            this.startActivity(nextIntent);
            finish();
        });
    }

    public void startOrder(String id, String start, String dest, String time, String sName, String dName, String sA, String dA){
        System.out.println("Ordering");
        Order o = new Order();
        o.setDest(dest);
        o.setId(id);
        o.setTimeWindow(time);
        o.setStart(start);
        o.setStartName(sName);
        o.setDestName(dName);
        o.setStartAddress(sA);
        o.setDestAddress(dA);
        String getUrl = "http://"+ IPUtil.getIp()+":8080/Passenger/startOrder";
        String reqJsonString = JSON.toJSONString(o);
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
                System.out.println("order success");
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String readLine = "";
                while ((readLine = reader.readLine()) != null) {
                    buffer.append(readLine);
                }
                is.close();
                reader.close();
                connection.disconnect();
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
