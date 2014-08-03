package cn.sdp.pkv.client.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.thrift.TException;

import cn.sdp.pkv.util.Configs;



public class ClientMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		PKVClient client = new PKVClient(0, 10000, 10, true);
		long t1 = System.currentTimeMillis();
		client.pointQuery();
		long t2 = System.currentTimeMillis();
		System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
//		for (int k = 11;k < 17;k++)
//		{
//			int s = 000000;
//			int e = s+300000;
//			while (s < 6000000)
//			{
//				PKVClient client = new PKVClient(s, e, k);
//				if (Configs.WITHINDEX)
//					client.insertEPKV();
//				else
//					client.insertCassandra(true);
//				Thread.sleep(5000);
//				s += 300000;
//				e += 300000; 
//				if (e > 6000000)
//					e = 6000000;
//				
//			}		
//	
//		}
	}
}

