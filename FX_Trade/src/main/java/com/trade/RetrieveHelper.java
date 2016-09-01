package com.trade;


/**
 * Created by sonalasija on 8/30/16.
 */

import java.util.*;
public class RetrieveHelper {


    HashMap<Double,Double> hashMap=new HashMap<Double,Double>();


    public HashMap printValues(){

        final Set<Map.Entry<Double, Double>> mapValues = Helper.orderMap.entrySet();
        final int maplength = mapValues.size();
        final Map.Entry<Double,Double>[] test = new Map.Entry[maplength];
        mapValues.toArray(test);
       // System.out.print("---- Last Key:-->"+test[maplength-1].getKey());
        //System.out.println("--- Last Value:--->"+test[maplength-1].getValue());
        hashMap.clear();
        hashMap.put(test[maplength-1].getKey(),test[maplength-1].getValue());
        return hashMap;

    }
}
