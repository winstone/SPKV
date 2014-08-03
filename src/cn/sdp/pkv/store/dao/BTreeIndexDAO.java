package cn.sdp.pkv.store.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.PriorityBlockingQueue;


import org.mapdb.BTreeMap;
import org.mapdb.DB;

import cn.sdp.pkv.data.type.OperateType;
import cn.sdp.pkv.pyramid.index.Pyramid;
import cn.sdp.pkv.store.MapDBConnector;
import cn.sdp.pkv.store.model.BTreeOperateResult;
import cn.sdp.pkv.store.model.BTreeOperator;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.store.model.ValueObject;


public class BTreeIndexDAO extends IndexDAO {
	private final List<BTreeMap<String, byte[]>> pts;
	private final DB db;
	private final OperatorExecutor executor = new OperatorExecutor();
	
	public BTreeIndexDAO(Pyramid p, String tableName)
	{
		this.db = MapDBConnector.getInstance().getDB();
		this.pts = new ArrayList<BTreeMap<String,byte[]>>();
		this.py = p;
		for (int i = 0;i < (p.getDimension()<<1);i++)
		{
			BTreeMap<String, byte[]> bt = this.db.getTreeMap(getIndexTableName(tableName, p.getDimension(),i));
			pts.add(bt);
		}
	}

	private String getIndexTableName(String tableName, int d, int i) {
		return new StringBuilder().append("pt:").append(tableName)
			.append(":").append(d).append(":").append(i).toString();
	}


	@Override
	protected ConcurrentNavigableMap<String, ValueObject> rangeResult(String key1, String key2) {
		String skey1 = key1;
		String skey2 = new BigDecimal(key2).add(new BigDecimal("0.00000001")).toString();

		BTreeMap<String, byte[]> ptree = getPTree(skey1);
		ConcurrentNavigableMap<String, byte[]> ret = ptree.subMap(skey1, skey2);
		ConcurrentSkipListMap<String, ValueObject> res = new ConcurrentSkipListMap<String, ValueObject>();
		for (Entry<String, byte[]> entry : ret.entrySet())
			res.put(entry.getKey(), new ValueObject(entry.getValue()));
		return res;
//		BTreeOperator op = new BTreeOperator(OperateType.QUERY, skey1, skey2);
//		executor.submitOperator(op);
//		return op.getObjResult();
	}

	@Override
	protected int operateDelete(IndexObject obj) {
		BTreeMap<String, byte[]> ptree = getPTree(obj.getpValue());
		ptree.remove(obj.getKey());
		return 0;
	}

	@Override
	protected int operateInsert(IndexObject iObj) {
		BTreeOperator op = new BTreeOperator(OperateType.INSERT, iObj.getKey(), iObj.getObj());
		executor.submitOperator(op);
		return op.getIntResult();
	}

	@Override
	protected int operateInsert(List<IndexObject> objs) {
		int ret = 0;
		for (IndexObject iObj : objs)
		{
			ret = operateInsert(iObj);
			if (ret != 0)
				return ret;
		}
		return ret;
	}
	
//	private void operateInsert(IndexObject iObj) {
//		BTreeMap<String, byte[]> ptree = getPTree(iObj.getpValue());
//		
//		insert(iObj.getKey(), iObj.getObj(), ptree);
//	}
//
//	private void insert(String key, ValueObject obj, BTreeMap<String, byte[]> ptree) {
//		byte[] buf = ptree.put(key, obj.toBytes());
//		if (buf != null)
//		{
//			ValueObject old = new ValueObject(buf);
//			if (obj.getTimestamp() < old.getTimestamp())
//				ptree.put(key, old.toBytes());
//		}
//	}

	
	private BTreeMap<String, byte[]> getPTree(Object pv) {
		Double p = 0.0;
		if (pv instanceof String)
			p = Double.parseDouble((String) pv);
		else
			p = (Double) pv;
		int i = p.intValue()/py.getInterval();
		return pts.get(i);
	}

	public void commit() {
		db.commit();
	}
	
	class OperatorExecutor {
		private final PriorityBlockingQueue<BTreeOperator> ops;
		
		public OperatorExecutor()
		{
			ops = new PriorityBlockingQueue<BTreeOperator>();
			new Thread(new Runnable() {
				@Override
				public void run() {
					BTreeOperator op;
					try {
						while (true)
						{
							op = ops.take();
							switch (op.getType())
							{
							case INSERT:
								insertOperate(op);
								break;
							case DELETE:
								break;
							case QUERY:
								queryOperate(op);
								break;
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		public void submitOperator(BTreeOperator op)
		{
			ops.add(op);
		}

		private void queryOperate(BTreeOperator op)
		{
			BTreeMap<String, byte[]> ptree = getPTree(op.getKey());
			ConcurrentNavigableMap<String, ValueObject> results = parseFrom(ptree.subMap(op.getKey(), op.getKey2()));
			BTreeOperateResult res = new BTreeOperateResult();
			res.setObjRets(results);
			op.setOperateResult(res);
		}
		
		private void insertOperate(BTreeOperator op) {
			String pv = op.getKey().split(";")[0];
			BTreeMap<String, byte[]> ptree = getPTree(pv);
			
			insert(op.getKey(), op.getObj(), ptree);
			BTreeOperateResult res = new BTreeOperateResult();
			res.setRet(0);
			op.setOperateResult(res);
		}

		private void insert(String key, ValueObject obj, BTreeMap<String, byte[]> ptree) {
			byte[] buf = ptree.put(key, obj.toBytes());
			if (buf != null)
			{
				ValueObject old = new ValueObject(buf);
				if (obj.getTimestamp() < old.getTimestamp())
					ptree.put(key, old.toBytes());
			}			
		}
		
		private ConcurrentNavigableMap<String, ValueObject> parseFrom(ConcurrentNavigableMap<String, byte[]> ret)
		{
			ConcurrentSkipListMap<String, ValueObject> res = new ConcurrentSkipListMap<String, ValueObject>();
			for (Entry<String, byte[]> entry : ret.entrySet())
				res.put(entry.getKey(), new ValueObject(entry.getValue()));

			return res;
		}
	}
}
