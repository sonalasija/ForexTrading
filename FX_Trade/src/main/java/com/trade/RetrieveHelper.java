package com.trade;

/**
 * Created by sonal asija on 8/30/16.
 */

import java.util.*;

public class RetrieveHelper {

	HashMap<Double, Double> hashMap = new HashMap<Double, Double>();

	public HashMap printValues() {

		final Set<Map.Entry<Double, Double>> mapValues = Helper.orderMap.entrySet();
		final int maplength = mapValues.size();
		final Map.Entry<Double, Double>[] test = new Map.Entry[maplength];
		mapValues.toArray(test);
		hashMap.clear();
		hashMap.put(test[maplength - 1].getKey(), test[maplength - 1].getValue());
		return hashMap;

	}
}
