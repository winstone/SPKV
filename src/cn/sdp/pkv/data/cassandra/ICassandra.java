package cn.sdp.pkv.data.cassandra;

import java.util.List;

import cn.sdp.pkv.dht.EndPoint;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;

public interface ICassandra {

	public abstract int createTable(String tableName);

	public abstract int insertObject(String tableName, SPKVObject obj, long ts);

	public abstract int insertObjects(String tableName, List<SPKVObject> objs,
			long ts);

	public abstract List<SPKVRow> getContent(String tableName, List<SPKVRow> res, List<String> retCols);

	public abstract List<EndPoint> getEndPoints();

}