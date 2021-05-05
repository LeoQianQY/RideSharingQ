package com.example.ridesharingq.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.R;
import com.example.ridesharingq.objects.Account;
import com.example.ridesharingq.objects.OrderList;
import com.example.ridesharingq.objects.Query;
import com.example.ridesharingq.objects.Tip;
import com.example.ridesharingq.runnables.DistanceRunnable;
import com.example.ridesharingq.runnables.OrderListRunnable;
import com.example.ridesharingq.runnables.QueryRunnable;
import com.example.ridesharingq.runnables.TipRunnable;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;


public class BindPassengerActivity extends AppCompatActivity {
    public String driverName = "";
    public String driverLoc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindp);
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        String did = intent.getStringExtra("did");
        driverLoc = intent.getStringExtra("driveLoc");
        TextView txt = findViewById(R.id.waitForDriver);
        txt.setText("Your driver is "+ did + " and he is on the way!");
        Button confirm = findViewById(R.id.confirmArrivalOfDriver);
        confirm.setOnClickListener(v -> new ConfirmArrivalTask().execute(pid));
        Button route = findViewById(R.id.mapWhileRiding);
        route.setOnClickListener(v -> mapIntent());
    }

    public void mapIntent(){
        final Intent intent = getIntent();
        String pickup = intent.getStringExtra("pickup");
        ArrayList<String> lst = new ArrayList<>();
        lst.add(driverLoc);
        lst.add(pickup);
        Intent nextIntent = new Intent();
        nextIntent.setClass(BindPassengerActivity.this, MapActivity.class);
        nextIntent.putExtra("lst", lst);
        this.startActivity(nextIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }

    private final class ConfirmArrivalTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("pid", params[0]);
            System.out.println("cancelOrdering");
            String getUrl = "http://"+ IPUtil.getIp()+":8080/PMatching/confirmArrival";
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
            nextIntent();
        }
    }

    public void nextIntent(){
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        String pickup = intent.getStringExtra("pickup");
        String dropoff = intent.getStringExtra("dropoff");
        Intent nextIntent = new Intent();
        nextIntent.setClass(BindPassengerActivity.this, BindPassenger2Activity.class);
        nextIntent.putExtra("pid", pid);
        nextIntent.putExtra("pickup", pickup);
        nextIntent.putExtra("dropoff", dropoff);
        nextIntent.putExtra("driveLoc", driverLoc);
        this.startActivity(nextIntent);
        finish();
    }
}
