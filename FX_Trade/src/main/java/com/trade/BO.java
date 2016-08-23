package com.trade;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
	    public  double currentPrice ;
	    public double bestPrice,worstPrice;
	    public ArrayList<Double> highestPrice=new ArrayList<Double>();
	    public ArrayList<Double> lowestPrice=new ArrayList<Double>();
	    JSONObject jObj = null;


	    public void tradeCurrency() {
	        ProcessBuilder pb = new ProcessBuilder(
	                "curl",
	                "-X",
	                "POST",
	                "-d",
	                "instrument=EUR_USD&units=14&side=buy&type=market",
	                "https://api-fxpractice.oanda.com/v1/accounts/9303001/orders", "-H",
	                "Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
	                "https://api-fxpractice.oanda.com/v1/accounts");

	        try {
	            executeQuery(pb,"","","none");
	        }catch (Exception e) {
	            System.out.println(e);
	        }

	    }


	    public void sellCurrency() {
	        ProcessBuilder pb = new ProcessBuilder(
	                "curl",
	                "-X",
	                "POST",
	                "-d",
	                "instrument=EUR_USD&units=14&side=sell&type=market",
	                "https://api-fxpractice.oanda.com/v1/accounts/9303001/orders", "-H",
	                "Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
	                "https://api-fxpractice.oanda.com/v1/accounts");

	        try {
	            executeQuery(pb,"","","none");
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

	    public void executeQuery(ProcessBuilder pb, String arrayName, String listName, String status) throws Exception {

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

	    public void readJSON(String arrayName, String itemName, String status) {

	        json=sb.toString();
	        jObj = new JSONObject(json);
	        JSONArray lineItems = jObj.getJSONArray(arrayName);
	        for (Object o : lineItems) {
	            JSONObject jsonLineItem = (JSONObject) o;
	            Double key = jsonLineItem.getDouble(itemName);
	            if (status.equals("currentprice")) {
	                currentPrice = key;
	            } else if (status.equals("last30_high")) {
	                highestPrice.add(key);
	            } else if(status.equals("last30_low")){
	                lowestPrice.add(key);
	            }
	        }
	    }

	    public void getLast30Candles(){
	        ProcessBuilder pb = new ProcessBuilder(
	                "curl",
	                "-X",
	                "GET",
	                "https://api-fxpractice.oanda.com/v1/candles?instrument=EUR_USD&count=30"
	        );

	        try {
	            executeQuery(pb,"candles","highAsk","last30_high");
	            executeQuery(pb,"candles","lowAsk","last30_low");
	        }catch (Exception e) {
	            System.out.println(e);
	        }
	    }

	    public void runEveryDay() {
	        getLastestPrice();
	        getLast30Candles();
	        bestPrice=Collections.max(highestPrice);
	        worstPrice=Collections.min(lowestPrice);

	        Date date=new Date();
	        Timer timer = new Timer();

	        timer.schedule(new TimerTask(){
	            public void run(){
	                System.out.println("Fetching some values and performing accordingly... "+new Date());

	                if(currentPrice > bestPrice){
	                    tradeCurrency();
	                    System.out.println("Purchased successfully...");
	                }else if(currentPrice < worstPrice){
	                    sellCurrency();
	                    System.out.println("Sold successfully...");
	                }else{
	                    System.out.println(" No transaction Performed today...");
	                }
	                System.out.println(" Todays Report :- ");
	                System.out.println(currentPrice+"<---currentprice---bestprice--->"+bestPrice);
	                System.out.println(currentPrice+"<---currentprice---worstprice--->"+worstPrice);
	                highestPrice.clear();
	                lowestPrice.clear();
	            }
	        },date, 24*60*60*1000);

	    }

	}
