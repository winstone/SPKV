package cn.sdp.pkv;

import java.util.Scanner;

import cn.sdp.pkv.data.PKVManager;
import cn.sdp.pkv.proxy.server.ProxyServer;
import cn.sdp.pkv.server.PKVServer;


public class PKVMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PKVServer server = new PKVServer();
		ProxyServer proxy = new ProxyServer();
		proxy.start();
		server.start();
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		PKVManager.getInstance().shutDown();
	}
}
