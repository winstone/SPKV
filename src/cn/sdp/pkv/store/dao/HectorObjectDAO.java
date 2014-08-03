package cn.sdp.pkv.store.dao;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.Set;
//
//import cn.sdp.handle.query.result.RangeQueryResult;
//import cn.sdp.pyramid.index.Pyramid;
//import cn.sdp.pyramid.model.IndexObject;
//import cn.sdp.pyramid.util.PyramidUtil;
//import cn.sdp.test.model.TestObject;
//
//import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
//import me.prettyprint.cassandra.serializers.DoubleSerializer;
//import me.prettyprint.cassandra.serializers.StringSerializer;
//import me.prettyprint.cassandra.service.CassandraHostConfigurator;
//import me.prettyprint.cassandra.service.FailoverPolicy;
//import me.prettyprint.hector.api.Cluster;
//import me.prettyprint.hector.api.HConsistencyLevel;
//import me.prettyprint.hector.api.Keyspace;
//import me.prettyprint.hector.api.beans.ColumnSlice;
//import me.prettyprint.hector.api.beans.HColumn;
//import me.prettyprint.hector.api.beans.OrderedRows;
//import me.prettyprint.hector.api.beans.Row;
//import me.prettyprint.hector.api.exceptions.HectorException;
//import me.prettyprint.hector.api.factory.HFactory;
//import me.prettyprint.hector.api.mutation.Mutator;
//import me.prettyprint.hector.api.query.RangeSlicesQuery;
//import me.prettyprint.hector.api.query.SliceQuery;
//
//
//
//
public class HectorObjectDAO {
//	private static HectorObjectDAO instance = new HectorObjectDAO();
//	private Cluster cluster;
//	private Keyspace keyspace;
//	private final int BASE_PAGE_SIZE = 18000;
//	protected StringSerializer serializer;
//	protected DoubleSerializer dserializer;
//	
//	private HectorObjectDAO()
//	{
//		CassandraHostConfigurator config = new CassandraHostConfigurator("192.168.3.247:9160");
//		config.setAutoDiscoverHosts(true);
//		cluster = HFactory.getOrCreateCluster("TestCluster", config);
//		ConfigurableConsistencyLevel consistency = new ConfigurableConsistencyLevel();
//		consistency.setDefaultReadConsistencyLevel(HConsistencyLevel.QUORUM);
//		consistency.setDefaultWriteConsistencyLevel(HConsistencyLevel.QUORUM);
//		keyspace = HFactory.createKeyspace("testdb", cluster, consistency, FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE);
//		serializer = StringSerializer.get();
//		dserializer = DoubleSerializer.get();
//	}
//	
//	public static HectorObjectDAO getInstance()
//	{
//		return instance;
//	}
//	
//	public int insertObject(List<TestObject> objs)
//	{
//		int dimension = objs.get(0).getCols().length;
//		int pageSize = (BASE_PAGE_SIZE/dimension);
//		pageSize -= pageSize % 100;
//		Mutator<String> mutator = HFactory.createMutator(keyspace, serializer);
//		Mutator<Double> mutator1 = HFactory.createMutator(keyspace, dserializer);
//		try
//		{
//			int c = 0;
//			long timestamp = System.currentTimeMillis();
//			for (TestObject obj : objs)
//			{
//				timestamp++;
//				String[] cols = obj.getCols();
//				for (int j = 1;j <= dimension;j++)
//				{
//					String colName = String.format("col%02d", j);
//					mutator.addInsertion(obj.getKey(), "clitests", HFactory.createStringColumn(colName, cols[j-1]));
//				}
//				mutator.addInsertion(obj.getKey(), "clitests", HFactory.createStringColumn("content", obj.getContent()));
//
//				String name = PyramidUtil.colsToString(cols);
//				name += ","+obj.getKey();
//				String value = obj.getContent();
//				mutator1.addInsertion(obj.getpValue(), "pvindex", HFactory.createStringColumn(name, value));
//				c++;
//				if (c%pageSize == 0)
//				{
//					mutator.execute();
//					mutator1.execute();
//				}
//			}
//			if (c%pageSize != 0)
//			{
//				mutator.execute();
//				mutator1.execute();
//			}
//		}
//		catch (HectorException e)
//		{
//			e.printStackTrace();
//			return -3;
//		}
//		return 0;
//	}
//
//	public List<TestObject> getPointResults(String pv, String[] cols) {
//		List<TestObject> results = new ArrayList<TestObject>();
//		SliceQuery<Double, String, String> query = HFactory.createSliceQuery(keyspace, dserializer, serializer, serializer);
//		ColumnSlice<String, String> result = query.setColumnFamily("pvindex").setKey(Double.parseDouble(pv)).setRange("", "", false, 100000).execute().get();
//		List<HColumn<String, String>> columns = result.getColumns();
////		System.out.println("Count: "+columns.size());
//		for (HColumn<String, String> col : columns)
//		{
//			String t = col.getName().substring(0, col.getName().lastIndexOf(","));
//			String key = getDataKey(col);
//			String[] resCols = t.split(",");
//			if (Arrays.equals(resCols, cols))
//			{
//				TestObject obj = new TestObject(key, cols, Double.parseDouble(pv), col.getValue());
//				results.add(obj);
//			}
//			
//		}
//		return results;
//	}
//
//	public RangeQueryResult getRangeResults(String key1, String key2, Object[] ql, Object[] qu) {
//		RangeQueryResult ret = new RangeQueryResult();
//		double k1 = Double.parseDouble(key1);
//		double k2 = Double.parseDouble(key2);
//		
//		RangeSlicesQuery<Double, String, String> query = HFactory.createRangeSlicesQuery(keyspace, dserializer, serializer, serializer);
//		OrderedRows<Double, String, String> result = query.setColumnFamily("pvindex").setKeys(k1, k2).setRange("", "", false, 100000).execute().get();
//		List<Row<Double, String, String>> rows = result.getList();
////		System.out.println("SubSize: "+columns.size());
//
//		for (Row<Double, String, String> row : rows)
//		{
//			List<HColumn<String, String>> resCols = row.getColumnSlice().getColumns();
//			ret.filterSize += resCols.size();
//			for (HColumn<String, String> col : resCols)
//			{
//				boolean flag = true;
//				String[] cols = col.getName().split(",");
//				cols = Arrays.copyOfRange(cols, 0, cols.length-1);
//				for (int j = 0;j < ql.length;j++)
//				{
//					if (Integer.parseInt(cols[j]) < (Integer)ql[j] || Integer.parseInt(cols[j]) > (Integer)qu[j])
//					{
//						flag = false;
//						break;
//					}
//				}
//				if (flag)
//				{
//					String key = getDataKey(col);
//					TestObject obj = new TestObject(key, cols, row.getKey(), col.getValue());
//					ret.results.add(obj);
//				}
//			}
//				
//		}
//		return ret;
//	}
//
//	
//	public Queue<IndexObject> getKNNResults(Pyramid py, String key1, String key2, double[] v, int K, Set<String> resultSet) {
//		Queue<IndexObject> results = new PriorityQueue<IndexObject>();
//		results = searchKNNResults(py, key1, key2, v, K, results, true);
//		return results;
//	}
//	
//	public Queue<IndexObject> getKNNResults(Pyramid py, String key1, String key2, double[] v, int K,
//									Queue<IndexObject> results, Set<String> resultSet) {
//		results = searchKNNResults(py, key1, key2, v, K, results, false);
//		return results;
//	}
//
//	private Queue<IndexObject> searchKNNResults(Pyramid py, String key1, String key2, double[] v, int K,
//									Queue<IndexObject> results, boolean isFirst) {
//		RangeSlicesQuery<Double, String, String> query = HFactory.createRangeSlicesQuery(keyspace, dserializer, serializer, serializer);
//
//		double k1 = Double.parseDouble(key1);
//		double k2 = Double.parseDouble(key2);
//		if (Double.compare(k1, k2)>0)
//			System.out.println(key1+"-"+key2);
//		OrderedRows<Double, String, String> result = query.setColumnFamily("pvindex").setKeys(k1, k2).setRange("", "", false, 100000).execute().get();
//		List<Row<Double, String, String>> rows = result.getList();
//
//		boolean flag = false;
//		for (Row<Double, String, String> row : rows)
//		{
//			List<HColumn<String, String>> resCols = row.getColumnSlice().getColumns();
//			for (HColumn<String, String> col : resCols)
//			{
//				String key = getDataKey(col);
//				String[] cols = col.getName().split(",");
//				cols = Arrays.copyOfRange(cols, 0, cols.length-1);
//				double[] norCols = py.getNormalization(PyramidUtil.stringsToObjs(cols));
//				
//				IndexObject obj = new IndexObject(key, row.getKey(), col.getValue(), norCols);
//				obj.setDistance(v);
//				if (isFirst)
//				{
//					if (results.size() < K)
//						results.add(obj);
//					else 
//					{
//						flag = true;
//						results.poll();
//						results.add(obj);
//					}
//				}
//				else
//				{
//					if (Double.compare(obj.getDistance(), results.peek().getDistance()) < 0)
//					{
//						results.poll();
//						results.add(obj);
//					}
//				}
//			}
//			if (isFirst && flag)
//				break;
//		}
//		return results;
//	}
//
//	private String getDataKey(HColumn<String, String> col) {
//		return col.getName().substring(col.getName().lastIndexOf(",")+1);
//	}
}
