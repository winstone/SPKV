package cn.sdp.pkv.pyramid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.sdp.pkv.data.query.result.RangeQueryResult;
import cn.sdp.pkv.data.type.TableOperate;
import cn.sdp.pkv.pyramid.index.BasePyramid;
import cn.sdp.pkv.pyramid.index.ExtendPyramid;
import cn.sdp.pkv.pyramid.index.Pyramid;
import cn.sdp.pkv.pyramid.index.Pyramid.PyramidRange;
import cn.sdp.pkv.pyramid.model.PyramidResult;
import cn.sdp.pkv.store.dao.BTreeIndexDAO;
import cn.sdp.pkv.store.memtable.IndexMemtable;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;

public class PyramidIndexTable {
	private final TableInfo tbInfo;
	private final PyramidConverter converter;
	private final BTreeIndexDAO index;
	private final IndexMemtable memindex;
	private final BlockingQueue<IndexObject> iObjs;
	private final Pyramid py;

	public PyramidConverter getConverter() {
		return converter; 
	}
	
	public TableInfo getTbInfo() {
		return tbInfo;
	}

//	public PyramidIndexTable(String tableName, Object[] lb, Object[] ub, int dimension, boolean type) {
//		this.tbInfo = new TableInfo(tableName, lb, ub, dimension, type);
//		if (type)
//			this.py = new ExtendPyramid(dimension, lb, ub);
//		else
//			this.py = new BasePyramid(dimension, lb, ub);
//		this.index = new BTreeIndexDAO(py, tableName);
//		this.memindex = new IndexMemtable(py);
//		converter = new PyramidConverter();
//		iObjs = new LinkedBlockingQueue<IndexObject>();
//	}

	public PyramidIndexTable(TableInfo tbInfo) {
		this.tbInfo = tbInfo;
		if (tbInfo.isType())
			this.py = new ExtendPyramid(tbInfo.getDimension(), tbInfo.getLb().toArray(), tbInfo.getUb().toArray());
		else
			this.py = new BasePyramid(tbInfo.getDimension(), tbInfo.getLb().toArray(), tbInfo.getUb().toArray());
		this.index = new BTreeIndexDAO(py, tbInfo.getTableName());
		this.memindex = new IndexMemtable(py);
		converter = new PyramidConverter();
		iObjs = new LinkedBlockingQueue<IndexObject>();
	}

	public List<SPKVRow> PointQuery(Object[] v, List<Integer> retCols) {
		String pv = getPyramidValue(v);
		List<IndexObject> pObjs = index.getPointResults(pv, v);
		List<IndexObject> mempObjs = memindex.getPointResults(pv, v);

		HashSet<String> resKeys = new HashSet<String>();
		List<SPKVRow> objs = new ArrayList<SPKVRow>();
		for (IndexObject pObj : mempObjs)
		{
			SPKVRow obj = this.converter.IndexToSPKVRow(pObj, retCols);
			objs.add(obj);
			resKeys.add(pObj.getOriginKey());
		}
		for (IndexObject pObj : pObjs)
		{
			if (!resKeys.contains(pObj.getOriginKey()))
			{
				SPKVRow obj = this.converter.IndexToSPKVRow(pObj, retCols);
				objs.add(obj);
			}
		}
		return objs;
	}
	
	public int PointQueryCount(Object[] v) {
		String pv = getPyramidValue(v);
		return index.getPointCountResults(pv, v); 
	}

	private String getPyramidValue(Object[] v) {
		return String.format("%.08f", py.getPyramidResult(v).getPv());
	}
		
	public List<SPKVRow> RangeQuery(Object[] ql, Object[] qu, List<Integer> retCols) {
		List<Pyramid.PyramidRange> ranges = py.getPyramidRange(ql, qu);

		int sum = 0;
		List<SPKVRow> objs = new ArrayList<SPKVRow>();
		for (Pyramid.PyramidRange range : ranges)
		{
			String key1 = String.format("%.08f", range.low);
			String key2 = String.format("%.08f", range.high);
//			System.out.println("P:"+low+","+high+",size:");
			
			RangeQueryResult res = index.getRangeResults(key1, key2, ql, qu);
			RangeQueryResult memRes = memindex.getRangeResults(key1, key2, ql, qu);

			HashSet<String> resKeys = new HashSet<String>();
			for (IndexObject iObj : memRes.results)
			{
				SPKVRow obj = this.converter.IndexToSPKVRow(iObj, retCols);
				objs.add(obj);
				resKeys.add(iObj.getOriginKey());
			}
			for (IndexObject iObj : res.results)
			{
				if (!resKeys.contains(iObj.getOriginKey()))
				{
					SPKVRow obj = this.converter.IndexToSPKVRow(iObj, retCols);
					objs.add(obj);
				}
			}
			sum += res.filterSize;
		}
//		System.out.println(sum);
		return objs;
	}

	public int RangeQueryCount(Object[] ql, Object[] qu) {
		List<Pyramid.PyramidRange> ranges = py.getPyramidRange(ql, qu);

		int sum = 0;
		for (Pyramid.PyramidRange range : ranges)
		{
			String key1 = String.format("%.08f", range.low);
			String key2 = String.format("%.08f", range.high);
//			System.out.println("P:"+low+","+high+",size:");
			
			sum += index.getRangeCountResults(key1, key2, ql, qu);
		}
//		System.out.println(res);
		return sum;
	}
	
	public List<SPKVRow> KNNQuery(Object[] v, int K, List<Integer> retCols) {
		Set<String> resSet = new HashSet<String>();
		Set<String> memResSet = new HashSet<String>();
		PyramidResult pr = py.getPyramidResult(v);
		double qpv = pr.getPv();
		String key1 = String.format("%.08f", qpv);
		String key2 = String.format("%.08f", Math.floor(qpv)+0.5);
		double[] norV = pr.getNorCols();
		Queue<IndexObject> res = index.getKNNResults(key1, key2, norV, K, resSet);
		Queue<IndexObject> memRes = memindex.getKNNResults(key1, key2, norV, K, memResSet);
		key1 = String.format("%.08f", Math.floor(qpv));
		key2 = String.format("%.08f", new BigDecimal(qpv).subtract(new BigDecimal(0.00000001)));
		res = index.getKNNResults(key1, key2, norV, K, res, resSet);
		memRes = memindex.getKNNResults(key1, key2, norV, K, memRes, memResSet);

		for (int i = 0;i < (py.getDimension()<<1);i++)
		{
			List<PyramidRange> ranges = py.getPyramidKNNRange(norV, res.peek().getDistance(), i);
			for (PyramidRange range : ranges)
			{
				key1 = String.format("%.08f", range.low);
				key2 = String.format("%.08f", range.high);
//				System.out.println("P:"+low+","+high+",size:");
				
				res = index.getKNNResults(key1, key2, norV, K, res, resSet);
				memRes = memindex.getKNNResults(key1, key2, norV, K, memRes, memResSet);
			}
		}

		List<SPKVRow> kObjs = new ArrayList<SPKVRow>();
		while (memRes.size() > 0)
		{
			res.add(memRes.poll());
			if (res.size() > K)
				res.poll();
		}
		int k = 0;
//		System.out.println("KNN size: "+res.size());
		while (res.size() > 0 && k++ < K)
		{
			IndexObject iObj = res.poll();
			kObjs.add(this.converter.IndexToSPKVRow(iObj, retCols));
		}
		Collections.reverse(kObjs);
		return kObjs;
	}
	
	public int syncInsertObject(IndexObject iObj)
	{
		return index.insertObject(iObj);
	}
	
	public void insertIndexObject(IndexObject iObj) {
		iObjs.add(iObj);
	}

	public void memInsertIndexObject(IndexObject iObj) {
		memindex.insertObject(iObj);		
	}	
	
	public void deleteIndexObject(IndexObject iObj) {
		index.deleteObject(iObj);		
	}

	public void memDeleteIndexObject(IndexObject iObj) {
		memindex.deleteObject(iObj);		
	}	
	
//	public void storeIndexObject() {
//		try {
//			IndexObject iObj = iObjs.take();
//			while (!isFinishObject(iObj))
//			{
//				index.insertObject(iObj);
//				iObj = iObjs.take();
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

//	private boolean isFinishObject(IndexObject iObj) {
//		return iObj.getObj() == null;
//	}

	public class PyramidConverter {
		public List<IndexObject> SPKVToIndex(List<SPKVObject> tObjs, long ts)
		{
			List<IndexObject> pObjs = new ArrayList<IndexObject>();
			for (SPKVObject obj : tObjs)
				pObjs.add(SPKVToIndex(obj, ts));
			return pObjs;
		}
		
		public IndexObject SPKVToIndex(SPKVObject tObj, long ts)
		{
			List<Integer> values = new ArrayList<Integer>();
			List<String> indexCols = tbInfo.getIndexColumn();
			for (int i = 0;i < indexCols.size();++i)
			{
				boolean f = false;
				for (SPColumn col : tObj.getCols())
				{
					if (col.getName().equals(indexCols.get(i)))
					{
						f = true;
						values.add(col.getValue());
						break;
					}
				}
				if (!f)
					values.add(-tbInfo.getUb().get(i));
			}
			PyramidResult res = py.getPyramidResult(values.toArray());
			return new IndexObject(tObj, res, ts);
		}
		
		public SPKVRow IndexToSPKVRow(IndexObject iObj, List<Integer> retCols)
		{
			List<String> allContents = iObj.getObj().getContentList();
			List<String> retContents = new ArrayList<String>();
			for (int i : retCols)
				retContents.add(allContents.get(i));
			
			return new SPKVRow(iObj.getOriginKey(), iObj.getObj().getCols(), retContents, 0.0);
		}

		public IndexObject OperateToIndex(TableOperate tOp) {
			PyramidResult res = py.getPyramidResult(tOp.getValueObject().getCols().toArray());
			return new IndexObject(tOp.getKey(), res.getPv(), tOp.getValueObject().getCols(),
					tOp.getValueObject().getContentList(), tOp.getValueObject().getTimestamp());
		}
	}

}
