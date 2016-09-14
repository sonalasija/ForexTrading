package com.trade;

/**
 * Created by sonal asija on 8/15/16.
 */
public interface FXInterface {

    public void buyCurrency();
    public void sellCurrency();
    public void getLast30Candles();
    public void getLastestPrice();
    public void executeQuery(ProcessBuilder pb, String arrayName,String listName, String status)throws Exception;
    public void readJSON(String arrayName, String itemName,String status);
    public void runEveryDay();

}
