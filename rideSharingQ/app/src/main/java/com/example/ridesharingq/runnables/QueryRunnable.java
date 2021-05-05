package com.example.ridesharingq.runnables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ridesharingq.objects.OrderList;
import com.example.ridesharingq.objects.Query;
import com.example.ridesharingq.objects.Tip;
import com.example.ridesharingq.objects.TipJson;
import com.example.ridesharingq.util.IPUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class QueryRunnable implements Runnable {
    private Query result;
    private String content;

    public QueryRunnable(String content){
        this.content = content;
    }

    @Override
    public void run() {
        result = getMatch(content);
    }

    public Query getResult(){
        return result;
    }

    public Query getMatch(String pid){
        System.out.println("getQuerying");
        String getUrl = "http://"+ IPUtil.getIp()+":8080/PMatching/getQuery";
        Query q = new Query();
        HashMap<String, String> map = new HashMap<>();
        map.put("pid", pid);
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
                q = JSONObject.toJavaObject(jsonObject, Query.class);
                is.close();
                reader.close();
                connection.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return q;
    }
}
