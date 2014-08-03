package cn.sdp.pkv.proxy.handler;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class RangeQueryHandler extends QueryHandler {
	private List<Integer> ql;
	private List<Integer> qu;

	public RangeQueryHandler(ExecutorService service, String tbName, List<SPColumn> ql, 
			List<SPColumn> qu, List<String> retCols) {
		super(service, retCols, tbName);
		this.ql = parseFromSPColumn(ql);
		this.qu = parseFromSPColumn(qu);
	}

	public List<SPKVObject> getRangeQueryResults() {
		sendRangeQueryRequest();
		return getQueryResponse();
	}

	public int getRangeQueryCountResults() {
		sendRangeQueryRequest();
		return getQueryCountResponse();
	}

	private void sendRangeQueryRequest() {
		Random ran = new Random();
		for (EndPoint ep : partMan.getEndPoints())
		{
			String ip = ep.getStringIP();
			if (!ip.equals(Configs.LOCAL_IP))
			{
				String repIp = PKVConverter.getStringIP(partMan.getMasterIps(ip).get(ran.nextInt(Configs.REPLICA_FACTOR-1)));
				objServ.submit(new RangeQueryRequest(ip, 0, repIp));
			}
			else
				objServ.submit(new RangeQueryLocal(0));
		}
	}
	
	public class RangeQueryLocal implements Callable<List<SPKVRow>> {
		private int mIp;
		
		public RangeQueryLocal(int mIp)
		{
			this.mIp = mIp;
		}

		@Override
		public List<SPKVRow> call(){
			// TODO Auto-generated method stub
			try
			{
				return local.rangeQuery(tableName, ql, qu, mIp, retCols);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return Collections.emptyList();
			}
		}
	}


	public class RangeQueryRequest extends PeerRequest implements Callable<List<SPKVRow>> {
		
		public RangeQueryRequest(String dest, int mIp, String repIp)
		{
			super(dest, mIp, repIp);
		}

		@Override
		public List<SPKVRow> call() throws Exception {
			// TODO Auto-generated method stub
			List<SPKVRow> res = null;
			TTransport transport = null;
			try {
				transport = openConnect();

				res = client.rangeQuery(tableName, ql, qu, mIp, retCols);
				
				transport.close();
			} catch (TTransportException e) {
				System.out.println(destIP+"Error");
				if (repIP == null)
				{
					e.printStackTrace();
					return Collections.emptyList();
				}
				
				System.out.println("retry "+ repIP);
				RangeQueryRequest request = new RangeQueryRequest(repIP, mIp, null);
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
