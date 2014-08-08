package cn.sdp.pkv.test;

import cn.sdp.pkv.client.PKVClient;

public class APITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Thread thread = new Thread(new PKVClient(0, 5, 0, false)); 
		thread.start();
	}

}
