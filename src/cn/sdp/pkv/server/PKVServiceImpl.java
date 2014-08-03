package cn.sdp.pkv.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.data.cassandra.HectorDAO;
import cn.sdp.pkv.data.cassandra.ICassandra;
import cn.sdp.pkv.proxy.CoordinatorProxy;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.PKVService.Iface;

public class PKVServiceImpl implements Iface {
	private CoordinatorProxy coordinator = CoordinatorProxy.getInstance();
	private final ICassandra cassandra = HectorDAO.getInstance();

	@Override
	public int createIndexTable(IndexInfo info) throws TException {
//		if (cassandra.createTable(info.getTableName()) != 0)
//			return -3;
		return coordinator.createIndexTable(info);
	}
	
	@Override
	public int insertObject(String tbName, SPKVObject obj) throws TException {
		// TODO Auto-generated method stub
		long ts = System.currentTimeMillis();
		//作为Coordinator，转发请求及本地操作
		if (cassandra.insertObject(tbName, obj, ts) != 0)
			cassandra.insertObject(tbName, obj, ts);
		else
			System.out.println("cassandra insert ok");

		return coordinator.insertObject(tbName, obj, ts);
	}
 
	@Override
	public int batchInsertObjects(Map<String, List<SPKVObject>> objs)
			throws TException {
		// TODO Auto-generated method stub
		long ts = System.currentTimeMillis();
		//作为Coordinator，转发请求及本地操作
//		for (String tbName : objs.keySet())
//		{
//			if (cassandra.insertObjects(tbName, objs.get(tbName), ts) != 0)
//				cassandra.insertObjects(tbName, objs.get(tbName), ts);
//			else
//				System.out.println("cassandra insert ok");
//		}

		return coordinator.insertObjects(objs, ts);
	}

	@Override
	public List<SPKVObject> knnQuery(String tbName, List<SPColumn> qv, int K, List<String> retCols)
			throws TException {
		// TODO Auto-generated method stub
		return coordinator.knnQuery(tbName, qv, K, retCols);
	}

	@Override
	public List<SPKVObject> pointQuery(String tbName, List<SPColumn> qv, List<String> retCols)
			throws TException {
		// TODO Auto-generated method stub
		return coordinator.pointQuery(tbName, qv, retCols);
	}

	@Override
	public int pointQueryCount(String tbName, List<SPColumn> qv)
			throws TException {
		return coordinator.pointQueryCount(tbName, qv);
	}

	@Override
	public List<SPKVObject> rangeQuery(String tbName, List<SPColumn> ql, List<SPColumn> qu, List<String> retCols)
			throws TException {
		// TODO Auto-generated method stub
		return coordinator.rangeQuery(tbName, ql, qu, retCols);
	}

	@Override
	public int rangeQueryCount(String tbName, List<SPColumn> ql, List<SPColumn> qu) throws TException {
		return coordinator.rangeQueryCount(tbName, ql, qu);
	}

}
