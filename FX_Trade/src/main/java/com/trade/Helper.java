package com.trade;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Helper implements Runnable {

	static Map<Double, Double> orderMap = new LinkedHashMap<Double, Double>();

	public void run() {

		try {
			getStreamingRates();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getStreamingRates() throws Exception {

		HttpClient httpClient = HttpClientBuilder.create().build();

		try {

			// Set these variables to whatever personal ones are preferred
			String domain = "https://stream-fxpractice.oanda.com";
			String access_token = "cba08e9d19b2846910876522b7e09b0a-bcf4ed08cff35e4a9ffd5dcbbb7e6f16";
			String account_id = "9303001";
			String instruments = "EUR_USD";

			HttpUriRequest httpGet = new HttpGet(
					domain + "/v1/prices?accountId=" + account_id + "&instruments=" + instruments);
			httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + access_token));

			HttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();

			if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
				InputStream stream = entity.getContent();
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));

				while ((line = br.readLine()) != null) {

					Object obj = JSONValue.parse(line);
					JSONObject tick = (JSONObject) obj;

					// unwrap if necessary
					if (tick.containsKey("tick")) {
						tick = (JSONObject) tick.get("tick");
					}

					// ignore heartbeats
					if (tick.containsKey("instrument")) {
						System.out.println("-------");

						String instrument = tick.get("instrument").toString();
						String time = tick.get("time").toString();
						double bid = Double.parseDouble(tick.get("bid").toString());
						double ask = Double.parseDouble(tick.get("ask").toString());
						orderMap.put(bid, ask);
						// bo.sendMap(orderMap);

					}
				}
			} else {
				// print error message
				String responseString = EntityUtils.toString(entity, "UTF-8");
				System.out.println(responseString);
			}

		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}
}
