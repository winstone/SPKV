package cn.sdp.pkv.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import cn.sdp.pkv.dht.PartitionerManager;
import cn.sdp.pkv.dht.ReplicaNodes;
import cn.sdp.pkv.proxy.handler.CreateTableHandler;
import cn.sdp.pkv.proxy.handler.InsertObjectHandler;
import cn.sdp.pkv.proxy.handler.KNNQueryHandler;
import cn.sdp.pkv.proxy.handler.PointQueryHandler;
import cn.sdp.pkv.proxy.handler.RangeQueryHandler;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class CoordinatorProxy {
	private static CoordinatorProxy intance = new CoordinatorProxy();
	private PartitionerManager partMan = PartitionerManager.getInstance();
	private ExecutorService service = Executors.newFixedThreadPool(24);

	private CoordinatorProxy() {}

	public static CoordinatorProxy getInstance() {
		return intance;
	}

	public int createIndexTable(IndexInfo info) {
		// TODO Auto-generated method stub
		CreateTableHandler handler = new CreateTableHandler(service, null, info);
		return handler.CreateIndexTable();
	}

	public int insertObject(String tbName, SPKVObject obj, long ts) {
		// TODO Auto-generated method stub
		Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap = new HashMap<ReplicaNodes, Map<String, List<SPKVObject>>>();
		putObjectToDestMap(destMap, tbName, obj);
		
		InsertObjectHandler handler = new InsertObjectHandler(service, tbName, destMap, ts);
		return handler.getInsertObjectResult();
	}
	
	public int insertObjects(Map<String, List<SPKVObject>> objs, long ts) {
		Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap = getDestMap(objs);
		
		InsertObjectHandler handler = new InsertObjectHandler(service, null, destMap, ts);
		return handler.getInsertObjectResult();
	}

	private Map<ReplicaNodes, Map<String, List<SPKVObject>>> getDestMap(Map<String, List<SPKVObject>> tObjs) {
		Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap = new HashMap<ReplicaNodes, Map<String, List<SPKVObject>>>();
		if (partMan.getEndPoints().size() == 1)
		{
			destMap.put(new ReplicaNodes(PKVConverter.getIntegerIP(Configs.LOCAL_IP)), tObjs);
			return destMap;
		}
		for (String tbName : tObjs.keySet())
		{
			for (SPKVObject obj : tObjs.get(tbName))
			{
				putObjectToDestMap(destMap, tbName, obj);
			}
		}
		return destMap;
	}

	private void putObjectToDestMap(Map<ReplicaNodes, Map<String, List<SPKVObject>>> destMap, String tbName,
			SPKVObject obj) {
		ReplicaNodes dest = partMan.getDataEndPoints(obj.getKey());
		
		if (!destMap.containsKey(dest))
		{
			Map<String, List<SPKVObject>> objs = new HashMap<String, List<SPKVObject>>();
			destMap.put(dest, objs);
		}
		if (!destMap.get(dest).containsKey(tbName))
		{
			List<SPKVObject> objs = new ArrayList<SPKVObject>();
			destMap.get(dest).put(tbName, objs);
		}
		destMap.get(dest).get(tbName).add(obj);
		
	}

	public List<SPKVObject> knnQuery(String tbName, List<SPColumn> qv, int K, List<String> retCols) {
		KNNQueryHandler handler = new KNNQueryHandler(service, tbName, qv, K, retCols);
		return handler.getKNNQueryResults();
	}

	public List<SPKVObject> pointQuery(String tbName, List<SPColumn> qv, List<String> retCols) {
		// TODO Auto-generated method stub
		PointQueryHandler handler = new PointQueryHandler(service, tbName, qv, retCols);
		return handler.getPointQueryResults();
	}

	public int pointQueryCount(String tbName, List<SPColumn> qv) {
		// TODO Auto-generated method stub
		PointQueryHandler handler = new PointQueryHandler(service, tbName, qv, new ArrayList<String>());
		return handler.getPointQueryCountResults();
	}

	public List<SPKVObject> rangeQuery(String tbName, List<SPColumn> ql, List<SPColumn> qu, List<String> retCols) {
		// TODO Auto-generated method stub
		RangeQueryHandler handler = new RangeQueryHandler(service, tbName, ql, qu, retCols);
		return handler.getRangeQueryResults();
	}

	public int rangeQueryCount(String tbName, List<SPColumn> ql, List<SPColumn> qu) {
		// TODO Auto-generated method stub
		RangeQueryHandler handler = new RangeQueryHandler(service, tbName, ql, qu, new ArrayList<String>());
		return handler.getRangeQueryCountResults();
	}

}
