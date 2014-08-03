package cn.sdp.pkv.client.concurrent;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import cn.sdp.pkv.thrift.SPKVObject;


class ConcurrentClient implements Callable<TestResult> {
	private PKVClient client;
	private final double updateRadio = 0.2;
	private final double pointRadio = 0.6;
	private final double rangeRadio = 0.1;
	private final int dist = 150000;
	private final int K = 50;
	private final Random ran = new Random();
	private int opCount;
		
	public ConcurrentClient(int from, int to, int offset, int op)
	{
		opCount = op;
		client = new PKVClient(from, to, offset, true);
	}

	public ConcurrentClient(List<SPKVObject> objs, int op)
	{
		opCount = op;
		client = new PKVClient(objs);
	}
	
	private int getOperator()
	{
		int op = ran.nextInt(10);
		if (op < updateRadio*10)
			return 0;
		else if (op < (pointRadio+updateRadio)*10)
			return 1;
		else if (op < (rangeRadio+pointRadio+updateRadio)*10)
			return 2;
		else
			return 3;
	}

	@Override
	public TestResult call() throws Exception {
		TestResult res = new TestResult();
		while (opCount > 0)				
		{
			long t1 = System.currentTimeMillis();
			switch (getOperator())
			{
			case 0:
				client.insertObject();
				long t2 = System.currentTimeMillis();
				res.updateTime.add(t2-t1);
				break;
			case 1:
				client.pointQuery();
				long t3 = System.currentTimeMillis();
				res.pointTime.add(t3-t1);
				break;
			case 2:
				client.rangeQuery(dist);
				long t4 = System.currentTimeMillis();
				res.rangeTime.add(t4-t1);
				break;
			case 3:
				client.knnQuery(K);
				long t5 = System.currentTimeMillis();
				res.knnTime.add(t5-t1);
				break;
			}
			if (opCount % 200 == 0)
				System.out.println(opCount);
			opCount--;
		}
		return res;
	}
	
}