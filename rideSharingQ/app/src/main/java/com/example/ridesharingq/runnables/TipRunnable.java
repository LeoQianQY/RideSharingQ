package com.example.ridesharingq.runnables;

import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.objects.Tip;
import com.example.ridesharingq.objects.TipJson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TipRunnable implements Runnable {
    private Tip[] result;
    private String content;

    public TipRunnable(String content){
        this.content = content;
    }

    @Override
    public void run() {
        result = getTips(content);
    }

    public Tip[] getResult(){
        return result;
    }

    public static Tip[] getTips(String content){
        Tip[] tj = null;
        String getUrl = "https://restapi.amap.com/v3/assistant/inputtips?output=json&city=010&keywords="+content+"&key=a5e3b81cf9de7a4191b1d6a4535f013f";
        try{
            URL url = new URL(getUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String readLine = "";
                while ((readLine = reader.readLine()) != null) {
                    buffer.append(readLine);
                }
                JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
                tj = JSONObject.toJavaObject(jsonObject, TipJson.class).getTips();
                System.out.println("buffer:"+buffer.toString());
                is.close();
                reader.close();
                connection.disconnect();
                return tj;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tj;
    }
}
