package cn.sdp.pkv.data.cassandra;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import java.util.List;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.thrift.TException;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;

public class HectorDAO implements ICassandra {
	private static HectorDAO instance = new HectorDAO();
	private Cluster cluster;
	private Keyspace keyspace;
	private StringSerializer serializer;
	private List<EndPoint> endPoints;
	
	private HectorDAO()
	{
		CassandraHostConfigurator config = new CassandraHostConfigurator(Configs.DB_IP);
		config.setAutoDiscoverHosts(true);
		cluster = HFactory.getOrCreateCluster("TestCluster", config);
		ConfigurableConsistencyLevel consistency = new ConfigurableConsistencyLevel();
		consistency.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
		consistency.setDefaultWriteConsistencyLevel(HConsistencyLevel.ONE);
		keyspace = HFactory.createKeyspace("testdb", cluster, consistency, FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE);
		serializer = StringSerializer.get();
		getTokenMap();
	}
	
	public static HectorDAO getInstance()
	{
		return instance;
	}

	@Override
	public int createTable(String tableName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<SPKVRow> getContent(String tableName, List<SPKVRow> objs, List<String> retCols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EndPoint> getEndPoints() {
		return endPoints;
	}
	
	private void getTokenMap()
	{
		endPoints = new ArrayList<EndPoint>();
		List<TokenRange> tokenRanges = cluster.describeRing(keyspace.getKeyspaceName());
        for (TokenRange tokenRange : tokenRanges) {
//            	System.out.print(tokenRange.getStart_token()+"-");
//            	System.out.println(tokenRange.getEnd_token());
//            	System.out.print("host");
//            	for (String host : tokenRange.getEndpoints()) {
//            		System.out.print(" "+host);
//            	}
//            	System.out.println();
            endPoints.add(new EndPoint(Long.parseLong(tokenRange.getEnd_token()), tokenRange.getEndpoints().get(0)));
        }
		Collections.sort(endPoints);
//		for (EndPoint ep : endPoints)
//			System.out.println(ep.getToken()+"-"+ep.getStringIP());
	}
	
	@Override
	public int insertObject(String tableName, SPKVObject obj, long ts) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertObjects(String tableName, List<SPKVObject> objs, long ts) {
		// TODO Auto-generated method stub
		Mutator<String> mutator = HFactory.createMutator(keyspace, serializer);
		try
		{
			for (SPKVObject obj : objs)
			{
				List<SPColumn> cols = obj.getCols();
				for (int i = 1;i <= cols.size();i++)
				{
					String colName = cols.get(i).getName();
					mutator.addInsertion(obj.getKey(), tableName, HFactory.createStringColumn(colName, cols.get(i-1)+""));
				}
				for (SPContent content : obj.getContent())
					mutator.addInsertion(obj.getKey(), tableName, HFactory.createStringColumn(
							content.getName(), content.getValue()));
				
			}
			mutator.execute();
		}
		catch (HectorException e)
		{
			e.printStackTrace();
			return -3;
		}
		return 0;
	}

}
