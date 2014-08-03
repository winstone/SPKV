package cn.sdp.pkv;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.sdp.pkv.client.PKVClient;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class ClientMain {
			
	public static void main(String[] args) throws InterruptedException {
//		ConcurrentTest();

		Thread thread = new Thread(new PKVClient(0, 100000, 0, false)); 
		thread.start();

//		for (int k = 10;k < 20;k++)
//		{
//			int s = 00000; 
//			int e = s+200000;
//			while (s < 5000000)
//			{
//				PKVClient client = new PKVClient(s, e, k, false);
//				client.run();
		
//				Thread.sleep(60000);
//				s += 200000;
//				e += 200000; 
//				if (e > 5000000)
//					e = 5000000; 
//			} 
//			
//		}
//		
	}
	
	private static void ConcurrentTest()	
	{
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0;i < 10;++i)
		{
			final int offset = i+5;
			Thread t1 = new Thread(new Runnable() {				
				public void run() {
					PKVClient client = new PKVClient(0, 10000, offset, true);
					long t1 = System.currentTimeMillis();
					client.insertData();
					long t2 = System.currentTimeMillis();
					System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
					
				}
			});
			threads.add(t1);
		}
		
		for (Thread th : threads)
			th.start();
	}
}

