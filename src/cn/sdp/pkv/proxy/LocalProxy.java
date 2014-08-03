package cn.sdp.pkv.proxy;

import java.util.List;
import java.util.Map;

import cn.sdp.pkv.data.PKVManager;
import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.data.cassandra.ICassandra;
import cn.sdp.pkv.dht.PartitionerManager;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class LocalProxy {
	private static LocalProxy instance = new LocalProxy();
	private PKVManager pMan = PKVManager.getInstance();
	private PartitionerManager partMan = PartitionerManager.getInstance();
	private ICassandra dbDao = CassandraDAO.getInstance();

	private LocalProxy() {}

	public static LocalProxy getInstance() {
		return instance;
	}
	
	public int createIndexTable(IndexInfo info) {
		int ret = 0;
		TableInfo mTbInfo = new TableInfo(info.getTableName(), info.getLb(),
				info.getUb(), info.getD(), Configs.IS_EXTEND, info.getIndexColumn(), info.getIndexContent());
		ret = pMan.createTable(mTbInfo);
		if (ret != 0)
			return ret;

		List<Integer> masterIps = partMan.getMasterIps(Configs.LOCAL_IP);
		System.out.println("mIps for "+Configs.LOCAL_IP);
		for (int mIp : masterIps)
		{
			System.out.print(":"+PKVConverter.getStringIP(mIp));
			TableInfo rTbInfo = new TableInfo(getReplicaTableName(info.getTableName(), mIp), info.getLb(),
				info.getUb(), info.getD(), Configs.IS_EXTEND, info.getIndexColumn(), info.getIndexContent());
			ret = pMan.createTable(rTbInfo);
			if (ret != 0)
				return ret;
		}
		return ret;
	}

	public int insertObject(String tbName, SPKVObject obj, long ts, int masterIp)
	{		
		return pMan.insertObject(getReplicaTableName(tbName, masterIp), obj, ts);
	}

	public int insertObjects(Map<String, List<SPKVObject>> objs, long ts, int masterIp) {
		for (String tbName : objs.keySet())
		{
			if (pMan.insertObjects(getReplicaTableName(tbName, masterIp), objs.get(tbName), ts) != 0)
				return -3;
		}
		return 0;
	} 
	
	public List<SPKVRow> knnQuery(String tbName, List<Integer> qv, int K, int masterIp
			, List<Integer> retCols)
	{
		return pMan.KNNQuery(getReplicaTableName(tbName, masterIp), qv.toArray(), K, retCols);
	}
	
	public List<SPKVRow> pointQuery(String tbName, List<Integer> qv, int masterIp, List<Integer> retCols) {
		List<SPKVRow> res = pMan.PointQuery(getReplicaTableName(tbName, masterIp), qv.toArray(), retCols);
		return res;
	}
	
	public int pointQueryCount(String tbName, List<Integer> qv, int masterIp) {
		return pMan.PointQueryCount(getReplicaTableName(tbName, masterIp), qv.toArray());
	}
	
	public List<SPKVRow> rangeQuery(String tbName, List<Integer> ql, List<Integer> qu, int masterIp
			, List<Integer> retCols) {
		List<SPKVRow> res = pMan.RangeQuery(getReplicaTableName(tbName, masterIp), ql.toArray(), 
				qu.toArray(), retCols);
		return res;
	}
	
	public int rangeQueryCount(String tbName, List<Integer> ql, List<Integer> qu, int masterIp) {
		return pMan.RangeQueryCount(getReplicaTableName(tbName, masterIp), ql.toArray(), qu.toArray());
	}
	
	private String getReplicaTableName(String tbName, int masterIp)
	{
		StringBuilder sb = new StringBuilder().append(tbName);
		if (masterIp != Configs.getLocalIntIP() && masterIp != 0)
		{
			sb.append(':').append(masterIp);
		}
		return sb.toString();
	}
}
