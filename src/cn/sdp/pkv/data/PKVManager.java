package cn.sdp.pkv.data;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import cn.sdp.pkv.data.type.TableOperate;
import cn.sdp.pkv.pyramid.PyramidIndexTable;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.store.MapDBConnector;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;

public class PKVManager {
	private static PKVManager instance = new PKVManager();
	private final TableManager tMan = TableManager.getInstance();
	private final ExecutorService service = Executors.newFixedThreadPool(24);
	
	private PKVManager()
	{}
	
	public static PKVManager getInstance()
	{
		return instance;
	}
	
	public int createTable(TableInfo tbInfo)
	{
		try {
			if (tMan.createNewTable(tbInfo) == 0)
				System.out.println("Created Table:"+tbInfo.getTableName()+"-"+tbInfo.getDimension());
			else
				System.out.println("The Table:"+tbInfo.getTableName()+"-"+tbInfo.getDimension()+"is Existed.");
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
		return 0;
	}
	
	public int insertObject(String tableName, SPKVObject obj, long ts) {
		try {
			final PyramidIndexTable table = tMan.getTableByName(tableName);
			final IndexObject iObj = insert(tableName, ts, table, obj);
			service.execute(new Runnable() {
				public void run() {
					table.syncInsertObject(iObj);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
		return 0;
	}
	
	public int insertObjects(String tableName, List<SPKVObject> tObjs, long ts) {
		try {
			PyramidIndexTable table = tMan.getTableByName(tableName);
			BlockingQueue<IndexObject> iObjs = new LinkedBlockingQueue<IndexObject>();
			startInsertService(table, iObjs);
			
			for (SPKVObject obj : tObjs)
			{
				IndexObject iObj = insert(tableName, ts, table, obj);
				iObjs.add(iObj);
			}
			IndexObject finishObj = new IndexObject();
			iObjs.add(finishObj);
//			insertFinishObject(table);
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
		System.out.println("Insert "+tObjs.size()+"objs");
		return 0;
	}

	private IndexObject insert(String tableName, long ts, PyramidIndexTable table, SPKVObject tObj) {
		IndexObject iObj = table.getConverter().SPKVToIndex(tObj, ts);
		TableOperate tOp = new TableOperate('1', tableName, tObj.getKey(), iObj.getObj());
//		table.memInsertIndexObject(iObj);
		return iObj;
	}

//	private void insertFinishObject(PyramidIndexTable table) {
//		IndexObject finishObj = new IndexObject();
//		table.insertIndexObject(finishObj);
//	}

//	private void startInsertService(final PyramidIndexTable table) {
//		service.execute(new Runnable() {
//			public void run() {
//				long t1 = System.currentTimeMillis();
//				table.storeIndexObject();
//				cmLog.setCheckPoint(System.currentTimeMillis());
//				long t2 = System.currentTimeMillis();
//				System.out.println(String.format("Insert ok, Time:%.3f",(t2-t1)/1000.0));
//			}
//		});
//	}
	
	private void startInsertService(final PyramidIndexTable table, final BlockingQueue<IndexObject> iObjs) {
		service.execute(new Runnable() {
			public void run() {
				long t1 = System.currentTimeMillis();
				int i = 0;
				try {
					IndexObject iObj = iObjs.take();
					while (iObj.getObj() != null)
					{
						i++;
						table.syncInsertObject(iObj);
						iObj = iObjs.take();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long t2 = System.currentTimeMillis();
				System.out.println(String.format("Insert ok, "+i+", Time:%.3f",(t2-t1)/1000.0));
			}
		});
	}
	
	public int PointQueryCount(String tableName, Object[] cols) {
		PyramidIndexTable table = tMan.getTableByName(tableName);
//		System.out.println("Point Query Count");
		return table.PointQueryCount(cols);
	}

	public List<SPKVRow> PointQuery(String tableName, Object[] cols, List<Integer> retCols) {
		PyramidIndexTable table = tMan.getTableByName(tableName);
//		System.out.println("Point Query");		
		return table.PointQuery(cols, retCols);
	}

	public List<SPKVRow> KNNQuery(String tableName, Object[] cols, int k, List<Integer> retCols) {
		PyramidIndexTable table = tMan.getTableByName(tableName);
//		System.out.println("KNN Query");
		return table.KNNQuery(cols, k, retCols);
	}

	public int RangeQueryCount(String tableName, Object[] ql, Object[] qu) {
		PyramidIndexTable table = tMan.getTableByName(tableName);
//		System.out.println("Point Query Count");
		return table.RangeQueryCount(ql, qu);
	}
	
	public List<SPKVRow> RangeQuery(String tableName, Object[] ql, Object[] qu, List<Integer> retCols) {
		PyramidIndexTable table = tMan.getTableByName(tableName);
//		System.out.println("Range Query");
		return table.RangeQuery(ql, qu, retCols);
	}
	
	public void shutDown()
	{
		MapDBConnector.getInstance().getLogdb().close();
		MapDBConnector.getInstance().getSysDB().close();
		MapDBConnector.getInstance().getDB().close();
		System.out.println("DB Closed");
		service.shutdown();
		System.out.println("Server Closed");
	}
}
