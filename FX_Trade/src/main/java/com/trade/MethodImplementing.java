package com.trade;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sonalasija on 8/15/16.
 */
public class MethodImplementing implements FXInterface {

	public StringBuilder sb;
	public String json = "",jsonPrice = "";
	public double highestPrice, lowestPrice, streamingBid, streamingAsk,currentPrice;;
	public ArrayList<Double> high_Price = new ArrayList<Double>();
	public ArrayList<Double> low_Price = new ArrayList<Double>();
	public JSONObject jObj = null;
	public RetrieveHelper retrieveHelper = new RetrieveHelper();
	public HashMap hashMap;
	public int count = 0;
	public boolean starttimer = true;

	public void buyCurrency() {
		buyOrSell("buy");
	}

	public void sellCurrency() {
		buyOrSell("sell");
	}

	public void buyOrSell(String param) {
		ProcessBuilder pb = new ProcessBuilder("curl", "-X", "POST", "-d",
				"instrument=EUR_USD&units=14&side=" + param + "&type=market",
				"https://api-fxpractice.oanda.com/v1/accounts/9303001/orders", "-H",
				"Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
				"https://api-fxpractice.oanda.com/v1/accounts");

		try {
			executeQuery(pb, "", "", "none");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void getLastestPrice() {
		ProcessBuilder pb = new ProcessBuilder("curl", "-X", "GET",
				"https://api-fxpractice.oanda.com/v1/prices?instruments=EUR_USD", "-H",
				"Authorization: Bearer cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16",
				"https://api-fxpractice.oanda.com/v1/accounts");

		try {
			executeQuery(pb, "prices", "ask", "currentprice");
		} catch (Exception e) {
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
			// System.out.println(line);
			sb.append(line + "\n");
		}
		if (!status.equals("none")) {
			readJSON(arrayName, listName, status);
			sb.setLength(0);
			jObj = null;
		}

	}

	public void readJSON(String arrayName, String itemName, String status) {

		json = sb.toString();
		jObj = new JSONObject(json);
		JSONArray lineItems = jObj.getJSONArray(arrayName);
		for (Object o : lineItems) {
			JSONObject jsonLineItem = (JSONObject) o;
			Double key = jsonLineItem.getDouble(itemName);
			if (status.equals("currentprice")) {
				currentPrice = key;
			} else if (status.equals("last30_high")) {
				high_Price.add(key);
			} else if (status.equals("last30_low")) {
				low_Price.add(key);
			}
		}
	}

	public void getLast30Candles() {
		ProcessBuilder pb = new ProcessBuilder("curl", "-X", "GET",
				"https://api-fxpractice.oanda.com/v1/candles?instrument=EUR_USD&count=30");

		try {
			executeQuery(pb, "candles", "highAsk", "last30_high");
			executeQuery(pb, "candles", "lowAsk", "last30_low");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// public Map sendMap(Map orderMap){
	// System.out.println("size of map in bo : "+orderMap.size());
	// return orderMap;
	// }

	public void getHashMapStreamingValues() {
		hashMap = retrieveHelper.printValues();
		Set set = hashMap.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			streamingAsk = (Double) me.getValue();
			streamingBid = (Double) me.getKey();
			System.out.println("hashmap ask--->" + streamingAsk);
			System.out.println("hashmap bid--->" + streamingBid);

		}
	}

	public void runEveryDay() {
		Date date = new Date();
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			public void run() {
				// getLastestPrice();
				getLast30Candles();
				getHashMapStreamingValues();
				highestPrice = Collections.max(high_Price);
				lowestPrice = Collections.min(low_Price);

				System.out.println("Fetching some values and performing accordingly... " + new Date());

				if (streamingAsk > highestPrice) {

					if (count == 0) {
						 buyCurrency();
						System.out.println("Purchased successfully...");
						starttimer = false;
					}
					if (count == 17280) {
						starttimer = true;
						count = 0;
					}
					if (count > 0)
						System.out.println("Only one purchase for the day");

				} else if (streamingAsk < lowestPrice) {
					   sellCurrency();
					System.out.println("Sold successfully...");
				} else {
					System.out.println(" No transaction Performed today...");
				}
				System.out.println(" Todays Report :- ");
				System.out.println(streamingAsk + "<---currentprice---bestprice--->" + highestPrice);
				System.out.println(streamingAsk + "<---currentprice---worstprice--->" + lowestPrice);
				high_Price.clear();
				low_Price.clear();
				if (!starttimer) {
					count++;
				}
				if (count == 17280) {
					starttimer = true;
				}
			}
		}, date, 5000);

	}

}
