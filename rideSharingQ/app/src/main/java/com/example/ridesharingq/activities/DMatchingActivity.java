package com.example.ridesharingq.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
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
import com.example.ridesharingq.runnables.QueryInfoRunnable;
import com.example.ridesharingq.runnables.QueryRunnable;
import com.example.ridesharingq.runnables.TipRunnable;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class DMatchingActivity extends AppCompatActivity {
    public ArrayList<String> driverLst = new ArrayList<>();
    public ArrayList<String> firstLst= new ArrayList<>();
    public ArrayList<String> secondLst= new ArrayList<>();
    public ArrayList<String> thirdLst= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmatching);
        TextView firstText = findViewById(R.id.firstText);
        TextView firstP = findViewById(R.id.firstP);
        TextView firstD = findViewById(R.id.firstD);
        TextView secondText = findViewById(R.id.secondText);
        TextView secondP = findViewById(R.id.secondP);
        TextView secondD = findViewById(R.id.secondD);
        TextView thirdText = findViewById(R.id.thirdText);
        TextView thirdP= findViewById(R.id.thirdP);
        TextView thirdD = findViewById(R.id.thirdD);
        Button prev1 = findViewById(R.id.prevFirst);
        Button prev2 = findViewById(R.id.prevSecond);
        Button prev3 = findViewById(R.id.prevThird);
        final Intent intent = getIntent();
        String id = intent.getStringExtra("did");
        getQuery(id);
        Button ub = findViewById(R.id.updateButton);
        Button cb = findViewById(R.id.cancelQueryButton);
        cb.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancel Order");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("yes", (dialog, which) -> new Thread(() -> cancelQuery(id)).start());
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(DMatchingActivity.this, "Return to Query",Toast.LENGTH_SHORT).show());
            builder.show();
        });
        ub.setOnClickListener(v -> {
            updateView(id);
        });
        LinearLayout fl = findViewById(R.id.foLayout);
        LinearLayout sl = findViewById(R.id.soLayout);
        LinearLayout tl = findViewById(R.id.toLayout);
        fl.setOnClickListener(v -> {
            confirmOrder(id, firstText, "1", firstP, firstD);
        });
        sl.setOnClickListener(v -> {
            confirmOrder(id, secondText, "2", secondP, secondD);
            ;
        });
        tl.setOnClickListener(v -> {
            confirmOrder(id, thirdText, "3",thirdP,thirdD);
        });
        prev1.setOnClickListener(v -> {mapIntent(firstText, firstLst);});
        prev2.setOnClickListener(v -> {mapIntent(secondText, secondLst);});
        prev3.setOnClickListener(v -> {mapIntent(secondText, secondLst);});
    }

    public void mapIntent(TextView v, ArrayList<String> passengerlst){
        if(v.getText().toString().equals("")){
            return;
        }
        ArrayList<String> lst = new ArrayList<>();
        Intent nextIntent = new Intent();
        nextIntent.setClass(DMatchingActivity.this, MapActivity.class);
        lst.add(driverLst.get(0));
        lst.add(driverLst.get(1));
        lst.add(passengerlst.get(0));
        lst.add(passengerlst.get(1));
        nextIntent.putExtra("lst", lst);
        this.startActivity(nextIntent);
    }

    public void confirmOrder(String id, TextView txt, String k, TextView P, TextView D){
        String str = txt.getText().toString();
        if(str.length() == 0){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose this order");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("yes", (dialog, which) -> new Thread(() -> bindOrder(id, txt, k, P, D)).start());
        builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(DMatchingActivity.this, "Return to Orderlist",Toast.LENGTH_SHORT).show());
        builder.show();
    }

    public void bindOrder(String did, TextView v, String k, TextView P, TextView D){
        String pid = v.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("did", did);
        map.put("pid", pid);
        map.put("loc", k);
        System.out.println("bindOrdering");
        String getUrl = "http://"+ IPUtil.getIp()+":8080/DMatching/bind";
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
                System.out.println("bind finished");
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder buffer = new StringBuilder();
                String readLine = "";
                while ((readLine = reader.readLine()) != null) {
                    buffer.append(readLine);
                }
                if(buffer.toString().equals(String.valueOf(0))){
                    Looper.prepare();
                    Toast.makeText(DMatchingActivity.this, "Seems that the order has been cancelled or expired, please update and try again",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                System.out.println(buffer.toString());
                is.close();
                reader.close();
                connection.disconnect();
                Intent nextIntent = new Intent();
                nextIntent.setClass(DMatchingActivity.this, BindDriverActivity.class);
                nextIntent.putExtra("did", did);
                nextIntent.putExtra("pid", pid);
                nextIntent.putExtra("pickup", P.getText().toString());
                nextIntent.putExtra("dropoff", D.getText().toString());
                if(k.equals("1")){
                    firstLst.add(driverLst.get(0));
                    nextIntent.putExtra("lst", firstLst);
                }
                if(k.equals("2")){
                    secondLst.add(driverLst.get(0));
                    nextIntent.putExtra("lst", secondLst);
                }
                if(k.equals("3")){
                    thirdLst.add(driverLst.get(0));
                    nextIntent.putExtra("lst", thirdLst);
                }
                this.startActivity(nextIntent);
                finish();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateView(String id){
        firstLst.clear();
        secondLst.clear();
        thirdLst.clear();
        TextView firstText = findViewById(R.id.firstText);
        TextView firstTime = findViewById(R.id.firstTime);
        TextView secondText = findViewById(R.id.secondText);
        TextView secondTime = findViewById(R.id.secondTime);
        TextView thirdText = findViewById(R.id.thirdText);
        TextView thirdTime = findViewById(R.id.thirdTime);
        TextView fp = findViewById(R.id.firstP);
        TextView fd = findViewById(R.id.firstD);
        TextView sp = findViewById(R.id.secondP);
        TextView sd = findViewById(R.id.secondD);
        TextView tp = findViewById(R.id.thirdP);
        TextView td = findViewById(R.id.thirdD);
        OrderListRunnable task = new OrderListRunnable(id);
        Thread olThread = new Thread(task);
        olThread.start();
        try {
            olThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OrderList ol = task.getResult();
        String fo = ol.getFirstOrder();
        if(fo == null){
            Toast.makeText(DMatchingActivity.this, "No Available Order, please try again later",Toast.LENGTH_SHORT).show();
            initialize(3);
            return;
        }
        String so = ol.getSecondOrder();
        String to = ol.getThirdOrder();
        ArrayList<String> fl = ol.getFirstList();
        ArrayList<String> sl = ol.getSecondList();
        ArrayList<String> tl = ol.getThirdList();
        firstText.setText(fo);
        fp.setText(fl.get(0));
        fd.setText(fl.get(1));
        firstTime.setText(timeForm(fl.get(2)));
        firstLst.add(fl.get(3));
        firstLst.add(fl.get(4));

        if(so == null){
            initialize(2);
            return;
        }
        secondText.setText(so);
        sp.setText(sl.get(0));
        sd.setText(sl.get(1));
        secondTime.setText(timeForm(sl.get(2)));
        secondLst.add(sl.get(3));
        secondLst.add(sl.get(4));

        if(to == null){
            initialize(1);
            return;
        }
        thirdText.setText(to);
        tp.setText(tl.get(0));
        td.setText(tl.get(1));
        thirdTime.setText(timeForm(tl.get(2)));
        thirdLst.add(tl.get(3));
        thirdLst.add(tl.get(4));
    }

    public void initialize(int num){
        TextView firstText = findViewById(R.id.firstText);
        TextView firstTime = findViewById(R.id.firstTime);
        TextView secondText = findViewById(R.id.secondText);
        TextView secondTime = findViewById(R.id.secondTime);
        TextView thirdText = findViewById(R.id.thirdText);
        TextView thirdTime = findViewById(R.id.thirdTime);
        TextView fp = findViewById(R.id.firstP);
        TextView fd = findViewById(R.id.firstD);
        TextView sp = findViewById(R.id.secondP);
        TextView sd = findViewById(R.id.secondD);
        TextView tp = findViewById(R.id.thirdP);
        TextView td = findViewById(R.id.thirdD);
        thirdText.setText("");
        tp.setText("");
        td.setText("");
        thirdTime.setText("");
        if(num>1){
            secondText.setText("");
            sp.setText("");
            sd.setText("");
            secondTime.setText("");
        }
        if(num>2){
            firstText.setText("");
            fp.setText("");
            fd.setText("");
            firstTime.setText("");
        }
    }

    public String timeForm(String oriTime){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = new Date(Long.parseLong(oriTime));
        String newTime = format.format(date);
        System.out.println(format.format(date));
        return newTime;
    }

    public void cancelQuery(String did){
        HashMap<String, String> map = new HashMap<>();
        map.put("did", did);
        System.out.println("cancelquerying");
        String getUrl = "http://"+IPUtil.getIp()+":8080/DMatching/cancel";
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
                System.out.println("cancelquery finished");
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

    public void getQuery(String did){
        QueryInfoRunnable task = new QueryInfoRunnable(did);
        Thread qThread = new Thread(task);
        qThread.start();
        try {
            qThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Query q = task.getResult();
        System.out.println(q);
        driverLst.add(q.getStart());
        driverLst.add(q.getDest());
        System.out.println("driverLst: "+ driverLst.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
