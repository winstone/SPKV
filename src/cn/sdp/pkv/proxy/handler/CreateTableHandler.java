package cn.sdp.pkv.proxy.handler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.thrift.transport.TTransport;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.dht.PartitionerManager;
import cn.sdp.pkv.proxy.IProxy;
import cn.sdp.pkv.proxy.LocalProxy;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.util.Configs;

public class CreateTableHandler extends MutationHandler {
	private final IndexInfo indexInfo;
	
	public CreateTableHandler(ExecutorService service, String tbName, IndexInfo info) {
		super(tbName, service);
		this.indexInfo = info;
	}
	
	public int CreateIndexTable()
	{
		sendCreateRequest();
		return getCreateResponse();
	}

	private void sendCreateRequest() {
		for (EndPoint ep : partMan.getEndPoints())
		{
			String ip = ep.getStringIP();
			if (!ip.equals(Configs.LOCAL_IP))
				intServ.submit(new CreateTableRequest(ip));
			else
				intServ.submit(new CreateTableLocal());
		}
	}

	private int getCreateResponse() {
		try
		{
			for (int i = 0; i < partMan.getEndPoints().size();i++)
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

	public class CreateTableLocal implements Callable<Integer> {
				
		@Override
		public Integer call() {
			try
			{
				if (local.createIndexTable(indexInfo) != 0)
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

	public class CreateTableRequest extends PeerRequest implements Callable<Integer> {
		
		public CreateTableRequest(String dest)
		{
			super(dest, 0, null);
		}

		@Override
		public Integer call() {
			try {
				TTransport transport = openConnect();
				
				client.createIndexTable(indexInfo);
				
				transport.close();
			} catch (Exception e) {
				e.printStackTrace();
				return -4;
			}
			return 0;
		}
	}
}
