package cn.sdp.pkv.proxy.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.data.cassandra.ICassandra;
import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.proxy.handler.PointQueryHandler.PointQueryRequest;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class KNNQueryHandler extends QueryHandler {
	private ICassandra dbDao = CassandraDAO.getInstance();
	private List<Integer> qv;
	private int K;

	public KNNQueryHandler(ExecutorService service, String tbName, List<SPColumn> qv, int K, List<String> retCols) {
		super(service, retCols, tbName);
		this.qv = parseFromSPColumn(qv);
		this.K = K;
	}

	public List<SPKVObject> getKNNQueryResults() {
		sendKNNQueryRequest();
		return getQueryResponse();
	}

	private void sendKNNQueryRequest() {
		Random ran = new Random();
		for (EndPoint ep : partMan.getEndPoints())
		{
			String ip = ep.getStringIP();
			if (!ip.equals(Configs.LOCAL_IP))
			{
				String repIp = PKVConverter.getStringIP(partMan.getMasterIps(ip).get(ran.nextInt(Configs.REPLICA_FACTOR-1)));
				objServ.submit(new KNNQueryRequest(ip, 0, repIp));
			}
			else
				objServ.submit(new KNNQueryLocal(0));
		}
	}

	@Override
	protected List<SPKVObject> getQueryResponse() {
		List<SPKVObject> results = new ArrayList<SPKVObject>(K);
		try
		{
			Queue<SPKVRow> queue = new PriorityQueue<SPKVRow>();
			for (int i = 0; i < partMan.getEndPoints().size();i++)
			{
				Future<List<SPKVRow>> ret = objServ.take();
				List<SPKVRow> kObjs = ret.get();
				for (SPKVRow kObj : kObjs)
				{
					queue.add(kObj);
					if (queue.size() > K)
						queue.poll();
				}
			}
			List<SPKVRow> rows = new ArrayList<SPKVRow>();
			
			while (!queue.isEmpty())
				rows.add(queue.poll());
			
			results.addAll(parseFromSPKVRow(rows));
			Collections.reverse(results);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public class KNNQueryLocal implements Callable<List<SPKVRow>> {
		private int mIp;
		
		public KNNQueryLocal(int mIp)
		{
			this.mIp = mIp;
		}

		@Override
		public List<SPKVRow> call(){
			// TODO Auto-generated method stub
			try
			{
				return local.knnQuery(tableName, qv, K, mIp, retCols);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return Collections.emptyList();
			}
		}
	}

	public class KNNQueryRequest extends PeerRequest implements Callable<List<SPKVRow>> {
		
		public KNNQueryRequest(String dest, int mIp, String repIp)
		{
			super(dest, mIp, repIp);
		}

		@Override
		public List<SPKVRow> call() {
			// TODO Auto-generated method stub
			List<SPKVRow> res = null;
			TTransport transport = null;
			try {
				transport = openConnect();

				res = client.peerKnnQuery(tableName, qv, K, mIp, retCols);
				
				transport.close();
			} catch (TTransportException e) {
				System.out.println(destIP+"Error");
				if (repIP == null)
				{
					e.printStackTrace();
					return Collections.emptyList();
				}
				
				System.out.println("retry "+ repIP);
				KNNQueryRequest request = new KNNQueryRequest(repIP, mIp, null);
				return request.call();
			} catch (TException e) {
				System.out.println(destIP+"Error");
				e.printStackTrace();
				return Collections.emptyList();
			}
			finally {
				transport.close();
			}
			return res;
		}
	}
}
