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


public class PMatchingActivity extends AppCompatActivity {
    public String driverName = "";
    public String driverLoc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmatching);
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        long timeExpected = intent.getLongExtra("timeExpected", 0);
        long timeNow = System.currentTimeMillis();
        long timeTotal = timeExpected-timeNow;
        Button cancelButton = findViewById(R.id.cancelOrder_button);
        cancelButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancel Order");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("yes", (dialog, which) -> new Thread(() -> cancelOrder(pid)).start());
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(PMatchingActivity.this, "Return to Order",Toast.LENGTH_SHORT).show());
            builder.show();
        });
        new PostTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pid);
        new orderExpireTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(timeTotal), pid);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }

    private final class orderExpireTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            long sleepTime = Long.parseLong(params[0]);
            System.out.println("sleeptime: " + sleepTime);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("pid", params[1]);
            System.out.println("cancelOrdering");
            String getUrl = "http://"+ IPUtil.getIp()+":8080/PMatching/orderExpire";
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
                    System.out.println("orderExpire finished");
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
            return params[1];
        }

        @Override
        protected void onPostExecute(String pid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PMatchingActivity.this);
            builder.setTitle("Order Expiration");
            builder.setMessage("Your expected time has passed, do you want to cancel the order?");
            builder.setPositiveButton("cancel", (dialog, which) -> cancelOrder(pid));
            builder.setNegativeButton("keep waiting", (dialogInterface, i) -> new Thread(() -> orderRenewal(pid)).start());
            builder.show();
        }
    }

    public void orderRenewal(String pid){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        long t = System.currentTimeMillis();
        long newTime = t + (long)5000;
        HashMap<String, String> map = new HashMap<>();
        map.put("pid", pid);
        map.put("newTime", String.valueOf(newTime));
        System.out.println("cancelOrdering");
        String getUrl = "http://"+IPUtil.getIp()+":8080/PMatching/orderRenewal";
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
                System.out.println("orderRenewal finished");
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
        System.out.println("beforeNEWTASK");
        new orderExpireTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(5000), pid);
    }

    private final class PostTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String did = "";
            QueryRunnable task = new QueryRunnable(params[0]);
            Thread qThread;
            System.out.println("enter postTask");
            while(true) {
                qThread = new Thread(task);
                try {
                    qThread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                qThread.start();
                try {
                    qThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Query q = task.getResult();
                if(q.getId() == null){
                    continue;
                }
                did = q.getId();
                driverLoc = q.getStart();
                break;
            }
            return did;
        }

        @Override
        protected void onPostExecute(String did) {
            super.onPostExecute(did);
            if(did.equals("")){
                return;
            }
            nextIntent(did);
        }
    }


    public void nextIntent(String did){
        final Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        String pickup = intent.getStringExtra("pickup");
        String dropoff = intent.getStringExtra("dropoff");
        Intent nextIntent = new Intent();
        nextIntent.setClass(PMatchingActivity.this, BindPassengerActivity.class);
        nextIntent.putExtra("pid", pid);
        nextIntent.putExtra("did", did);
        nextIntent.putExtra("pickup", pickup);
        nextIntent.putExtra("dropoff", dropoff);
        nextIntent.putExtra("driveLoc", driverLoc);
        this.startActivity(nextIntent);
        finish();
    }

    public void cancelOrder(String pid){
        HashMap<String, String> map = new HashMap<>();
        map.put("pid", pid);
        System.out.println("cancelOrdering");
        String getUrl = "http://"+IPUtil.getIp()+":8080/PMatching/cancel";
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
                System.out.println("cancelOrder finished");
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
        this.finish();
    }
}
