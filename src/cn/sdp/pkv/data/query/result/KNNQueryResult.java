package cn.sdp.pkv.data.query.result;

import java.util.Queue;

import cn.sdp.pkv.store.model.IndexObject;



public class KNNQueryResult {
	public Queue<IndexObject> results;
	public int filterSize;
	public double distance;
	
	public KNNQueryResult() {
		super();
		this.filterSize = 0;
	}
}
