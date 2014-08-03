package cn.sdp.pkv.proxy;

import java.util.List;
import java.util.Map;

import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.thrift.SPKVObject;

public interface IProxy {
	public abstract int createIndexTable(IndexInfo info);

	public abstract int insertObject(String tbName, SPKVObject obj, long ts, int masterIp);

	public abstract int insertObjects(Map<String, List<SPKVObject>> objs, long ts, int masterIp);

//	public abstract List<KNNObject> knnQuery(String tbName, List<Integer> qv, int K);

	public abstract List<SPKVRow> pointQuery(String tbName, List<Integer> qv);

	public abstract int pointQueryCount(String tbName, List<Integer> qv);

	public abstract List<SPKVRow> rangeQuery(String tbName, List<Integer> ql, List<Integer> qu);

	public abstract int rangeQueryCount(String tbName, List<Integer> ql, List<Integer> qu);


}