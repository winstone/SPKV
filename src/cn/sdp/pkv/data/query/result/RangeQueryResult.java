package cn.sdp.pkv.data.query.result;

import java.util.ArrayList;
import java.util.List;

import cn.sdp.pkv.store.model.IndexObject;


public class RangeQueryResult {
	public List<IndexObject> results;
	public int filterSize;
	
	public RangeQueryResult() {
		super();
		results = new ArrayList<IndexObject>();
		this.filterSize = 0;
	}
}
