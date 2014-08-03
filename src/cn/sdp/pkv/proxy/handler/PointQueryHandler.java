package cn.sdp.pkv.proxy.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class PointQueryHandler extends QueryHandler {
	private List<Integer> qv;

	public PointQueryHandler(ExecutorService service, String tbName, List<SPColumn> qv, List<String> retCols) {
		super(service, retCols, tbName);
		this.qv = parseFromSPColumn(qv);
	}

	public List<SPKVObject> getPointQueryResults() {
		sendPointQueryRequest();
		return getQueryResponse();
	}

	public int getPointQueryCountResults() {
		sendPointQueryRequest();
		return getQueryCountResponse();
	}
		
	private void sendPointQueryRequest() {
		Random ran = new Random();
		for (EndPoint ep : partMan.getEndPoints())
		{
			String ip = ep.getStringIP();
			if (!ip.equals(Configs.LOCAL_IP))
			{
				String repIp = PKVConverter.getStringIP(partMan.getMasterIps(ip).get(ran.nextInt(Configs.REPLICA_FACTOR-1)));
				objServ.submit(new PointQueryRequest(ip, 0, repIp));
			}
			else
				objServ.submit(new PointQueryLocal(0));
		}
	}
	
	public class PointQueryLocal implements Callable<List<SPKVRow>> {
		private int mIp;
		
		public PointQueryLocal(int mIp)
		{
			this.mIp = mIp;
		}

		@Override
		public List<SPKVRow> call(){
			// TODO Auto-generated method stub
			try
			{
				return local.pointQuery(tableName, qv, mIp, retCols);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return Collections.emptyList();
			}
		}
	}

	public class PointQueryRequest extends PeerRequest implements Callable<List<SPKVRow>> {
		
		public PointQueryRequest(String dest, int mIp, String rIp)
		{
			super(dest, mIp, rIp);
		}

		@Override
		public List<SPKVRow> call() throws Exception {
			// TODO Auto-generated method stub
			List<SPKVRow> res = null;
			TTransport transport = null;
			try {
				transport = openConnect();

				res = client.pointQuery(tableName, qv, mIp, retCols);

				transport.close();
			} catch (TTransportException e) {
				System.out.println(destIP+"Error");
				if (repIP == null)
				{
					e.printStackTrace();
					return Collections.emptyList();
				}
				
				System.out.println("retry "+ repIP);
				PointQueryRequest request = new PointQueryRequest(repIP, mIp, null);
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
