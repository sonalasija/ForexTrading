package com.trade;

/**
 * Created by sonalasija on 8/15/16.
 */
public interface FXImplementation {

    public void tradeCurrency();
    public void getLastDayCandles();
    public void getLastestPrice();
    public void executeQuery(ProcessBuilder pb, String arrayName,String listName, String status)throws Exception;
    public void readJSON(String arrayName, String itemName,String status);
    public void runEveryDay();

}
