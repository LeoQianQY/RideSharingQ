package com.example.ridesharingq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Account;
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
import java.util.HashMap;

public class DriverActivity extends AppCompatActivity {
    final String beijing_loc = "010";
    private String startName = "";
    private String endName = "";
    private HashMap<String, Tip> startMap = new HashMap<>();
    private HashMap<String, Tip> endMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        final Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        AutoCompleteTextView autoText = findViewById(R.id.autotext);
        AutoCompleteTextView autoDes = findViewById(R.id.autoDestination);
        autoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Time before input: "+System.currentTimeMillis());
                if(s.length()==0){
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
                System.out.println("Time after input: "+System.currentTimeMillis());
                int len = result.length;
                String[] resultLoc = new String[len - 1];
                for(int i = 0;i<len-1;i++){
                    System.out.println("resultloc:  "+result[i].getName());
                    resultLoc[i] = result[i].getName();
                    startMap.put(result[i].getName(), result[i]);
                }
                System.out.println("startset:   " + startMap.entrySet());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DriverActivity.this, android.R.layout.simple_list_item_1, resultLoc);
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
                    endMap.put(result[i].getName(), result[i]);
                }
                System.out.println("endset:   " + endMap.entrySet());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DriverActivity.this, android.R.layout.simple_list_item_1, resultLoc);
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
        Button cancel = findViewById(R.id.cancelQuery);
        Button confirm = findViewById(R.id.confirmQuery);
        cancel.setOnClickListener(v -> {
            this.finish();
        });
        confirm.setOnClickListener(v -> {
            System.out.println("this is the startMap*****:    "+startMap.entrySet());
            System.out.println("this is the endMap*****:    "+endMap.entrySet());
            System.out.println(startName);
            System.out.println(endName);
            if(!startMap.containsKey(startName) || ! endMap.containsKey(endName)){
                Toast.makeText(DriverActivity.this, "Invalid startLocation or destination!", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> startQuery(startMap.get(startName).getLocation(), endMap.get(endName).getLocation(), id, startName, endName,
                    startMap.get(startName).getAddress(), endMap.get(endName).getAddress())).start();
            Intent nextIntent = new Intent();
            nextIntent.setClass(DriverActivity.this, DMatchingActivity.class);
            nextIntent.putExtra("did", id);
            this.startActivity(nextIntent);
            finish();
        });
    }

    public void startQuery(String startL, String destL,String id,String startN, String destN,   String startA, String destA){
        System.out.println("Querying");
        Query q = new Query();
        q.setDest(destL);
        q.setId(id);
        q.setStart(startL);
        q.setdName(destN);
        q.setsName(startN);
        q.setsAddress(startA);
        q.setdAddress(destA);
        String getUrl = "http://"+ IPUtil.getIp()+":8080/Driver/startQuery";
        String reqJsonString = JSON.toJSONString(q);
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
                System.out.println("query success");
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
