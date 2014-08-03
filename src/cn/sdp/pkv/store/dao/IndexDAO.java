package cn.sdp.pkv.store.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.BTreeMap;


import cn.sdp.pkv.data.query.result.RangeQueryResult;
import cn.sdp.pkv.pyramid.index.Pyramid;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.store.model.ValueObject;

public abstract class IndexDAO {
	protected Pyramid py;

	public Queue<IndexObject> getKNNResults(String key1, String key2, double[] v, int K, Set<String> resultSet) {
		Queue<IndexObject> results = new PriorityQueue<IndexObject>();
//		results = searchKNNResults(key1, key2, v, K, results, resultSet, true);
		ConcurrentNavigableMap<String, ValueObject> res = rangeResult(key1, key2);
		
		boolean flag = false;
		for (Entry<String, ValueObject> entry : res.entrySet())
		{
			String[] keys = entry.getKey().split(";");
			List<Integer> cols = entry.getValue().getCols();
			double[] norCols = py.getNormalization(cols.toArray());
			IndexObject obj = new IndexObject(keys[1], Double.parseDouble(keys[0]), cols, entry.getValue().getContentList(), norCols);
			obj.setDistance(v);
			if (results.size() < K)
			{
				results.add(obj);
				resultSet.add(obj.getOriginKey());
			}
			else 
			{
				flag = true;
				IndexObject p = results.poll();
				results.add(obj);
				resultSet.remove(p.getOriginKey());
				resultSet.add(obj.getOriginKey());
			}
			if (flag)
				break;
		}
		return results;
	}

	public Queue<IndexObject> getKNNResults(String key1, String key2, double[] v, int K,
									Queue<IndexObject> results, Set<String> resultSet) {
//		results = searchKNNResults(key1, key2, v, K, results, resultSet, false);
		ConcurrentNavigableMap<String, ValueObject> res = rangeResult(key1, key2);

		for (Entry<String, ValueObject> entry : res.entrySet())
		{
			String[] keys = entry.getKey().split(";");
			List<Integer> cols = entry.getValue().getCols();
			double[] norCols = py.getNormalization(cols.toArray());
			IndexObject obj = new IndexObject(keys[1], Double.parseDouble(keys[0]), cols,
					entry.getValue().getContentList(), norCols);
			obj.setDistance(v);
			if (results.size() == 0)
			{
				results.add(obj);
				resultSet.add(obj.getOriginKey());
			}
			if (Double.compare(obj.getDistance(), results.peek().getDistance()) < 0)
			{
				if (!resultSet.contains(obj.getOriginKey()))
				{
					results.add(obj);
					resultSet.add(obj.getOriginKey());
					if (results.size() > K)
					{
						IndexObject p = results.poll();
						resultSet.remove(p.getOriginKey());
					}
				}
			}
		}
		return results;
	}

//	private Queue<IndexObject> searchKNNResults(String key1, String key2, double[] v, int K,
//									Queue<IndexObject> results, Set<String> resultSet, boolean isFirst) {
//		ConcurrentNavigableMap<String, ValueObject> res = rangeResult(key1, key2);
//
//		boolean flag = false;
//		for (Entry<String, ValueObject> entry : res.entrySet())
//		{
//			String[] keys = entry.getKey().split(";");
//			Object[] cols = entry.getValue().getCols();
//			double[] norCols = py.getNormalization(cols);
//			IndexObject obj = new IndexObject(keys[1], Double.parseDouble(keys[0]), cols, entry.getValue().getContent(), norCols);
//			obj.setDistance(v);
//			if (isFirst)
//			{
//				if (results.size() < K)
//				{
//					results.add(obj);
//					resultSet.add(obj.getOriginKey());
//				}
//				else 
//				{
//					flag = true;
//					IndexObject p = results.poll();
//					results.add(obj);
//					resultSet.remove(p.getOriginKey());
//					resultSet.add(obj.getOriginKey());
//				}
//			}
//			else
//			{
//				if (results.size() == 0)
//				{
//					results.add(obj);
//					resultSet.add(obj.getOriginKey());
//				}
//				if (Double.compare(obj.getDistance(), results.peek().getDistance()) < 0)
//				{
//					if (!resultSet.contains(obj.getOriginKey()))
//					{
//						results.add(obj);
//						resultSet.add(obj.getOriginKey());
//						if (results.size() > K)
//						{
//							IndexObject p = results.poll();
//							resultSet.remove(p.getOriginKey());
//						}
//					}
//				}
//			}
//			if (isFirst && flag)
//				break;
//		}
//		return results;
//	}

	public List<IndexObject> getPointResults(String pv, Object[] v) {
		// TODO Auto-generated method stub
		ConcurrentNavigableMap<String, ValueObject> results = rangeResult(pv, pv);

		List<IndexObject> retList = new ArrayList<IndexObject>();
		for (Entry<String, ValueObject> entry : results.entrySet())
		{
			List<Integer> cols = entry.getValue().getCols();
			boolean flag = true;
			for (int j = 0;j < v.length;j++)
			{
				if (cols.get(j) >= 0 && cols.get(j) != (Integer)v[j])
				{
					flag = false;
					break;
				}
			}
			if (flag)
			{
				String[] keys = entry.getKey().split(";");
				IndexObject obj = new IndexObject(keys[1], Double.parseDouble(keys[0]), entry.getValue().getCols(),
						entry.getValue().getContentList()); 
				retList.add(obj);
			}
		}
		return retList;
	}

	public int getPointCountResults(String pv, Object[] v) {
		// TODO Auto-generated method stub
		ConcurrentNavigableMap<String, ValueObject> results = rangeResult(pv, pv);

		int res = 0;
		for (Entry<String, ValueObject> entry : results.entrySet())
		{
			List<Integer> cols = entry.getValue().getCols();
			boolean flag = true;
			for (int j = 0;j < v.length;j++)
			{
				if (cols.get(j) >= 0 && cols.get(j) != (Integer)v[j])
				{
					flag = false;
					break;
				}
			}
			if (flag)
				res++;
		}
		return res;
	}
	
	public RangeQueryResult getRangeResults(String key1, String key2, Object[] ql, Object[] qu) {
		// TODO Auto-generated method stub
		ConcurrentNavigableMap<String, ValueObject> results = rangeResult(key1, key2);

		RangeQueryResult ret = new RangeQueryResult();
		ret.filterSize += results.size();
		for (Entry<String, ValueObject> entry : results.entrySet())
		{
			String[] keys = entry.getKey().split(";");
			List<Integer> cols = entry.getValue().getCols();
			boolean flag = true;
			for (int j = 0;j < ql.length;j++)
			{
//				System.out.println("Object Size:"+cols.size()+"-"+qu.length);
				if (cols.get(j) >= 0 && (cols.get(j) < (Integer)ql[j] || cols.get(j) > (Integer)qu[j]))
				{
					flag = false;
					break;
				}
			}
			if (flag)
			{
				String key = keys[1];
				IndexObject obj = new IndexObject(key, Double.parseDouble(keys[0]), cols, entry.getValue().getContentList());
				ret.results.add(obj);
			}
		}
		return ret;
	}

	public int getRangeCountResults(String key1, String key2, Object[] ql, Object[] qu) {
		// TODO Auto-generated method stub
		ConcurrentNavigableMap<String, ValueObject> results = rangeResult(key1, key2);

		int res = 0;
		for (Entry<String, ValueObject> entry : results.entrySet())
		{
			List<Integer> cols = entry.getValue().getCols();
			boolean flag = true;
			for (int j = 0;j < ql.length;j++)
			{
				if (cols.get(j) >= 0 && (cols.get(j) < (Integer)ql[j] || cols.get(j) > (Integer)qu[j]))
				{
					flag = false;
					break;
				}
			}
			if (flag)
			{
				res++;
			}
		}
		return res;
	}

	public int deleteObject(IndexObject obj)
	{
		return operateDelete(obj);
	}

	public int insertObject(List<IndexObject> objs) {
		return operateInsert(objs);
	}

	public int insertObject(IndexObject iObj)
	{
		return operateInsert(iObj);
	}

	protected abstract ConcurrentNavigableMap<String, ValueObject> rangeResult(String key1, String key2);
	protected abstract int operateInsert(List<IndexObject> iObj);	
	protected abstract int operateInsert(IndexObject iObj);	
	protected abstract int operateDelete(IndexObject obj);
}