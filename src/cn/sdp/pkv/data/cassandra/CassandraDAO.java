package cn.sdp.pkv.data.cassandra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class CassandraDAO implements ICassandra {
	private static CassandraDAO instance = new CassandraDAO();
	private Client client;
	private List<EndPoint> endPoints;

	private CassandraDAO()
	{
		try {
			TSocket socket = new TSocket(Configs.DB_IP, Configs.DB_PORT);
			socket.setTimeout(60000);
			TTransport tr = new TFramedTransport(socket);
			TProtocol proto = new TBinaryProtocol(tr);
			client = new Cassandra.Client(proto);
			tr.open();
			if(!tr.isOpen())
			{
				System.out.println("failed to connect Cassandra server!");
			}
			else
			{
				System.out.println("Connected to Cassandra server!");
				client.set_keyspace("testdb");
				getTokenMap();
			}
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static CassandraDAO getInstance()
	{
		return instance;
	}
	
	public int createTable(String tableName)
	{
		CfDef cf = new CfDef("testdb", tableName);
		Map<String, String> compactOption = new HashMap<String, String>();
		compactOption.put("sstable_size_in_mb", "10");
		Map<String, String> compressOption = new HashMap<String, String>();
		compressOption.put("sstable_compression", "SnappyCompressor");
		compressOption.put("chunk_length_kb", "64");
		cf.setCompaction_strategy("LeveledCompactionStrategy").setCompaction_strategy_options(compactOption)
			.setCompression_options(compressOption).setKey_validation_class("UTF8Type")
			.setComparator_type("UTF8Type").setDefault_validation_class("UTF8Type");
		try {
			client.system_add_column_family(cf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -3;
		}
		return 0;
	}
	
	public int insertObject(String tableName, SPKVObject obj, long ts)
	{
		try {
			Map<ByteBuffer, Map<String, List<Mutation>>> insertBatch = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
			
			insertRow(tableName, ts, insertBatch, obj);
			
			client.batch_mutate(insertBatch, ConsistencyLevel.ONE);
			insertBatch.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -3;
		}
		return 0;
	}
	
	public int insertObjects(String tableName, List<SPKVObject> objs, long ts)
	{
		int ret = 0;
		try {
			Map<ByteBuffer, Map<String, List<Mutation>>> insertBatch = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
			for (SPKVObject obj : objs)
			{
				insertRow(tableName, ts, insertBatch, obj);
			}
			client.batch_mutate(insertBatch, ConsistencyLevel.ONE);
			insertBatch.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -3;
		}
		return ret;
	}
	
	public List<SPKVRow> getContent(String tableName, List<SPKVRow> objs, List<String> retCols)
	{
		try {
			ColumnParent parent = new ColumnParent(tableName);
			List<ByteBuffer> keyBuffers = new ArrayList<ByteBuffer>();
			for (SPKVRow obj : objs)
				keyBuffers.add(PKVConverter.toByteBuffer(obj.getKey()));
			SlicePredicate pred = new SlicePredicate();
			for (String colName : retCols)
				pred.addToColumn_names(PKVConverter.toByteBuffer(colName));
			Map<ByteBuffer, List<ColumnOrSuperColumn>> res = client.multiget_slice(keyBuffers, parent, pred, ConsistencyLevel.ONE);

			for (SPKVRow obj : objs)
			{
				List<ColumnOrSuperColumn> c = res.get(PKVConverter.toByteBuffer(obj.getKey()));
				for (ColumnOrSuperColumn column : c)
				{
					Column col = column.getColumn();
					obj.addToContent(PKVConverter.toString(col.value));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objs;
	}

	private void insertRow(String tableName, long ts,
			Map<ByteBuffer, Map<String, List<Mutation>>> insertBatch,
			SPKVObject obj) {
		ByteBuffer keyBuffer = PKVConverter.toByteBuffer(obj.getKey());
		Map<String, List<Mutation>> map = getRowMutations(tableName, ts, obj);
		insertBatch.put(keyBuffer, map);
	}

	private Map<String, List<Mutation>> getRowMutations(String tableName,
			long ts, SPKVObject obj) {
		Map<String, List<Mutation>> map = new HashMap<String, List<Mutation>>();
		List<Mutation> mutations = new ArrayList<Mutation>();
		int i = 1;
		for (SPColumn col : obj.getCols())
		{
			Column column = new Column(PKVConverter.toByteBuffer(col.getName()));
			column.setValue(PKVConverter.toByteBuffer(col+""));
			column.setTimestamp(ts);
			mutations.add(new Mutation().setColumn_or_supercolumn(new ColumnOrSuperColumn().setColumn(column)));
		} 
		for (SPContent con : obj.getContent())
		{
			Column contentColumn = new Column(PKVConverter.toByteBuffer(con.getName()));
			contentColumn.setValue(PKVConverter.toByteBuffer(con.getValue()));
			contentColumn.setTimestamp(ts);
			mutations.add(new Mutation().setColumn_or_supercolumn(new ColumnOrSuperColumn().setColumn(contentColumn)));
		}
		map.put(tableName, mutations);
		return map;
	}
	
	public List<EndPoint> getEndPoints()
	{
		return endPoints;
	}
	
	private void getTokenMap()
	{
		endPoints = new ArrayList<EndPoint>();
		try {
			Map<String, String> map = client.describe_token_map();
			for (String token : map.keySet())
			{
//				System.out.println(token);
//				System.out.println("host "+map.get(token));
				endPoints.add(new EndPoint(Long.parseLong(token), map.get(token)));
			}
			Collections.sort(endPoints);
//			for (EndPoint ep : endPoints)
//				System.out.println(ep.getToken()+"-"+ep.getStringIP());
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
