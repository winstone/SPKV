package cn.sdp.pkv.store.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sdp.pkv.pyramid.model.PyramidResult;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;

public class IndexObject implements Comparable<IndexObject>, Serializable {
	private static final long serialVersionUID = 8298188678755713398L;
	private String key;
	private ValueObject obj;
	
	private double pValue;
	private double[] normalCols;
	private double distance;
		
	public IndexObject(String key, double pv, List<Integer> cols, List<String> contents) {
		this.key = String.format("%.08f;", pv);
		this.key += key;
		this.obj = new ValueObject(cols, contents, 0);
		this.pValue = pv;
	}

	public IndexObject(String key, double pv, List<Integer> cols, List<String> list, double[] normalCols) {
		this.key = String.format("%.08f;", pv);
		this.key += key;
		this.obj = new ValueObject(cols, list, 0);
		this.pValue = pv;
		this.normalCols = normalCols;
	}

	public IndexObject(SPKVObject obj, PyramidResult res, long ts) {
		this.key = String.format("%.08f;", res.getPv());
		this.key += obj.getKey();
		List<Integer> cols = new ArrayList<Integer>();
		for (SPColumn spCol : obj.getCols())
			cols.add(spCol.getValue());
		List<String> contents = new ArrayList<String>();
		for (SPContent spContent : obj.getContent())
			contents.add(spContent.getValue());
		this.obj = new ValueObject(cols, contents, ts);
		this.normalCols = res.getNorCols();
		this.pValue = res.getPv();
	}
	
	public IndexObject() {
		this.key = null;
		this.obj = null;
		this.normalCols = null;
	}

	public IndexObject(String key, double pv, List<Integer> cols, List<String> content, long ts) {
		this.key = String.format("%.08f;", pv);
		this.key += key;
		this.obj = new ValueObject(cols, content, ts);
		this.pValue = pv;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}	
	
	public String getOriginKey()
	{
		return key.substring(key.indexOf(";")+1);
	}
	
	public void setObj(ValueObject obj) {
		this.obj = obj;
	}
	public ValueObject getObj() {
		return obj;
	}

	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	public double[] getNormalCols() {
		return normalCols;
	}
	public void setNormalCols(double[] normalCols) {
		this.normalCols = normalCols;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double setDistance(double[] o)
	{
		distance = 0; 
		for (int i = 0;i < normalCols.length;i++)
		{
			if (normalCols[i] >= 0 && o[i] >= 0)
			{
				double d = o[i]-normalCols[i];
				distance += d*d;
			}
		}
		
		return Math.sqrt(distance);
	}	

	public int compareTo(IndexObject o) {
		if (Double.compare(distance, o.getDistance()) > 0)
			return -1;
		else
			return 1;
	}

}
