package com.trade;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sonalasija on 8/15/16.
 */
public class BO implements FXImplementation {

    public StringBuilder sb;
    public String json="";
    public String jsonPrice="";
    public  double candlePrice ;
    public  double currentPrice ;
    JSONObject jObj = null;


   
    public void tradeCurrency() {
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-X",
                "POST",
                "-d",
                "instrument=EUR_USD&units=11&side=buy&type=market",
                "https://api-fxpractice.oanda.com/v1/accounts/9303001/orders", "-H",
                "Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
                "https://api-fxpractice.oanda.com/v1/accounts");

        try {
            executeQuery(pb,"","","none");
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    
    public void getLastDayCandles() {
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-X",
                "GET",
                "https://api-fxpractice.oanda.com/v1/candles?instrument=EUR_USD&count=1"
        );

        try {
            executeQuery(pb,"candles","closeAsk","candleprice");
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    
    public void getLastestPrice() {
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-X",
                "GET",
                "https://api-fxpractice.oanda.com/v1/prices?instruments=EUR_USD",
                "-H",
                "Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
                "https://api-fxpractice.oanda.com/v1/accounts"
        );

        try {
            executeQuery(pb,"prices","ask","currentprice");
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    
    public void executeQuery(ProcessBuilder pb, String arrayName,String listName, String status)throws Exception {
        Process p;
        p = pb.start();
        InputStream is = p.getInputStream();
        String line;
        sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
              System.out.println(line);
            sb.append(line + "\n");
        }
        if(!status.equals("none")) {
            readJSON(arrayName, listName, status);
            sb.setLength(0);
            jObj = null;
        }

    }

    
    public void readJSON(String arrayName, String itemName,String status) {
        json=sb.toString();
        jObj = new JSONObject(json);
        JSONArray lineItems = jObj.getJSONArray(arrayName);
        for (Object o : lineItems) {
            JSONObject jsonLineItem = (JSONObject) o;
            Double key = jsonLineItem.getDouble(itemName);
            if(status.equals("candleprice")){
                candlePrice=key;
            }else if(status.equals("currentprice")){
                currentPrice=key;
            }

        }

    }

    
    public void runEveryDay() {
        getLastestPrice();
        getLastDayCandles();

        Date date=new Date();
        Timer timer = new Timer();

        timer.schedule(new TimerTask(){
            public void run(){
                System.out.println("Performing Purchase "+new Date());

                if(currentPrice >= candlePrice){
                    tradeCurrency();
                }
                System.out.println("Purchased successfully....");
            }
        },date, 24*60*60*1000);

    }
}
