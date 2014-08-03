package cn.sdp.pkv.proxy.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import cn.sdp.pkv.proxy.LocalProxy;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.thrift.PKVLocalService.Iface;

public class PKVLocalServiceImpl implements Iface {
	private LocalProxy local = LocalProxy.getInstance();

	@Override
	public int createIndexTable(IndexInfo info) throws TException {
		// TODO Auto-generated method stub
		return local.createIndexTable(info);
	}

	@Override
	public int batchInsertObjects(Map<String, List<SPKVObject>> objs,
			long timestamp, int masterIp) throws TException {
		// TODO Auto-generated method stub
		
		return local.insertObjects(objs, timestamp, masterIp);
	}

	@Override
	public int insertObject(String tbName, SPKVObject obj, long timestamp, int masterIp) throws TException {
		// TODO Auto-generated method stub

		return local.insertObject(tbName, obj, timestamp, masterIp);
	}

	@Override
	public List<SPKVRow> peerKnnQuery(String tbName, List<Integer> qv, int K, int masterIp,
			List<Integer> retCols) throws TException {
		// TODO Auto-generated method stub
		return local.knnQuery(tbName, qv, K, masterIp, retCols);
	}

	@Override
	public List<SPKVRow> pointQuery(String tbName, List<Integer> qv,
			int masterIp, List<Integer> retCols) throws TException {
		// TODO Auto-generated method stub
		return local.pointQuery(tbName, qv, masterIp, retCols);
	}

	@Override
	public int pointQueryCount(String tbName, List<Integer> qv, int masterIp)
			throws TException {
		// TODO Auto-generated method stub
		return local.pointQueryCount(tbName, qv, masterIp);
	}

	@Override
	public List<SPKVRow> rangeQuery(String tbName, List<Integer> ql, List<Integer> qu,
			int masterIp, List<Integer> retCols) throws TException {
		// TODO Auto-generated method stub
		return local.rangeQuery(tbName, ql, qu, masterIp, retCols);
	}

	@Override
	public int rangeQueryCount(String tbName, List<Integer> ql,
			List<Integer> qu, int masterIp) throws TException {
		// TODO Auto-generated method stub
		return local.rangeQueryCount(tbName, ql, qu, masterIp);
	}

}
