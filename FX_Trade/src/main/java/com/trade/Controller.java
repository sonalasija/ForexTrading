package com.trade;

/**
 * Created by sonalasija on 8/10/16.
 */

public class Controller {

	public static void main(String[] args) {

		Helper helper = new Helper();
		new Thread(helper).start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new MethodImplementing().runEveryDay();

	}
}
