package com.example.ridesharingq.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Query;
import com.example.ridesharingq.runnables.QueryRunnable;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class BindDriverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdriver);
        final Intent intent = getIntent();
        String did = intent.getStringExtra("did");
        String pickup = intent.getStringExtra("pickup");
        TextView placeToGo = findViewById(R.id.placeToGo);
        placeToGo.setText(pickup);
        Button arrivalP = findViewById(R.id.confirmArrivalAtStart);
        arrivalP.setOnClickListener(v -> new ConfirmArrivalAtPTask().execute(did, "startPConfirm"));
        Button prev =findViewById(R.id.prevWhileDriving);
        prev.setOnClickListener(v -> mapIntent());
    }

    public void mapIntent(){
        final Intent intent = getIntent();
        ArrayList<String> oldlst = intent.getStringArrayListExtra("lst");
        ArrayList<String> lst = new ArrayList<>();
        lst.add(oldlst.get(2));
        lst.add(oldlst.get(0));
        Intent nextIntent = new Intent();
        nextIntent.setClass(BindDriverActivity.this, MapActivity.class);
        nextIntent.putExtra("lst", lst);
        this.startActivity(nextIntent);
    }

    private final class ConfirmArrivalAtPTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("did", params[0]);
            map.put("loc", params[1]);
            String getUrl = "http://"+ IPUtil.getIp()+":8080/bindDriver/arrive";
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
                    System.out.println("confirmArrival finished");
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
                    return buffer.toString();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals(String.valueOf(1))){
                Toast.makeText(BindDriverActivity.this, "Passenger hasn't confirmed!",Toast.LENGTH_SHORT).show();
            }else if(result.equals(String.valueOf(2))){
                nextIntent();
            }else{
                Toast.makeText(BindDriverActivity.this, "Something's wrong!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void nextIntent(){
        final Intent intent = getIntent();
        String did = intent.getStringExtra("did");
        ArrayList<String> oldlst = intent.getStringArrayListExtra("lst");
        String dropoff = intent.getStringExtra("dropoff");
        Intent nextIntent = new Intent();
        nextIntent.setClass(BindDriverActivity.this, BindDriver2Activity.class);
        nextIntent.putExtra("did", did);
        nextIntent.putExtra("dropoff", dropoff);
        nextIntent.putExtra("lst", oldlst);
        this.startActivity(nextIntent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
