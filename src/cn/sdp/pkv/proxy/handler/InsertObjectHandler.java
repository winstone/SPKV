package cn.sdp.pkv.proxy.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.thrift.transport.TTransport;

import cn.sdp.pkv.dht.ReplicaNodes;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class InsertObjectHandler extends MutationHandler {
	private Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap;
	private long ts;
	
	public InsertObjectHandler(ExecutorService service, String tbName, 
			Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap, long ts) {
		super(tbName, service);
		this.destMap = destMap;
		this.ts = ts;
	}
	
	public int getInsertObjectResult()
	{
		for (ReplicaNodes rNodes : destMap.keySet())
		{
			Map<String, List<SPKVObject>> destObjs = destMap.get(rNodes);
			sendInsertRequest(rNodes.getMaster(), rNodes.getMaster(), destObjs, ts);
			for (int repIp : rNodes.getReplicas())
				sendInsertRequest(repIp, rNodes.getMaster(), destObjs, ts);
		}
		return getInsertResponse(destMap.size());
	}

	private void sendInsertRequest(int destIP, int mIp, Map<String, List<SPKVObject>> destObjs, long ts) {
		if (destIP != Configs.getLocalIntIP())
			intServ.submit(new InsertObjectsRequest(PKVConverter.getStringIP(destIP), mIp, destObjs));
		else
			intServ.submit(new InsertObjectsLocal(destObjs, mIp));		
	}

	private int getInsertResponse(int reqCount) {
		try
		{
			for (int i = 0; i < reqCount;i++)
			{
				Future<Integer> ret = intServ.take();
				if (ret.get() != 0)
					return ret.get();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return -4;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -4;
		}
		return 0;
	}

	public class InsertObjectsLocal implements Callable<Integer> {
		private Map<String, List<SPKVObject>> objs;
		private int mIp;
		
		public InsertObjectsLocal(Map<String, List<SPKVObject>> objs, int mIp)
		{
			this.objs = objs;
			this.mIp = mIp;
		}
		
		@Override
		public Integer call() {
			try
			{
				if (local.insertObjects(objs, ts, mIp) != 0)
					return -3;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return -3;
			}
			return 0;
		}
	}

	public class InsertObjectsRequest extends PeerRequest implements Callable<Integer> {
		private Map<String, List<SPKVObject>> testobjs;
		
		public InsertObjectsRequest(String dest, int mIp, Map<String, List<SPKVObject>> testobjs)
		{
			super(dest, mIp, null);
			this.testobjs = testobjs;
		}

		@Override
		public Integer call() {
			int ret = 0;
			TTransport transport = null;
			try {
				transport = openConnect();
				client.batchInsertObjects(testobjs, ts, mIp);
				
			} catch (Exception e) {
				e.printStackTrace();
				ret = -4;
			} finally {
				transport.close();
			}
			return ret;
		}
	}
}
