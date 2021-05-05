package com.example.ridesharingq.runnables;

import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.objects.DistanceJson;
import com.example.ridesharingq.objects.Tip;
import com.example.ridesharingq.objects.TipJson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DistanceRunnable implements Runnable {
    private double result;
    private String origin;
    private String destination;

    public DistanceRunnable(String origin, String destination){
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public void run() {
        result = getDistance(origin, destination);
    }

    public double getResult(){
        return result;
    }

    public static double getDistance(String origin, String destination){
        System.out.println("getDistance{}");
        String url1 = "http://restapi.amap.com/v3/distance?origins=" + origin + "&destination=" + destination + "&output=json"
                + "&key=a5e3b81cf9de7a4191b1d6a4535f013f";
        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder buffer = new StringBuilder();
                String readLine = "";
                while ((readLine = reader.readLine()) != null) {
                    buffer.append(readLine);
                }
                JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
                System.out.println("buffer:" + buffer.toString());
                DistanceJson dj = JSONObject.toJavaObject(jsonObject, DistanceJson.class);
                String result = dj.getResults()[0].getDistance();
                System.out.println(result);
                is.close();
                reader.close();
                connection.disconnect();
                return Double.parseDouble(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
