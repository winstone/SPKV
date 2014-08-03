package cn.sdp.pkv.client.concurrent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TestResult {
	public List<Long> updateTime = new ArrayList<Long>();
	public List<Long> queryTime = new ArrayList<Long>();
	public List<Long> pointTime = new ArrayList<Long>();
	public List<Long> rangeTime = new ArrayList<Long>();
	public List<Long> knnTime = new ArrayList<Long>();
	public int rangeSize = 0;
	
	
	public void add(TestResult res) {
		this.updateTime.addAll(res.updateTime);
		this.pointTime.addAll(res.pointTime);
		this.queryTime.addAll(res.queryTime);
		this.rangeTime.addAll(res.rangeTime);
		this.knnTime.addAll(res.knnTime);
		this.rangeSize += res.rangeSize;
	}

	public double getUpdateTime() {
		// TODO Auto-generated method stub
		return getAvg(updateTime);
//		return getMidNum(updateTime);
	}

	public double getPointTime() {
		// TODO Auto-generated method stub
		return getAvg(pointTime);
//		return getMidNum(pointTime);
	}

	public double getRangeTime() {
		// TODO Auto-generated method stub
		return getAvg(rangeTime);
//		return getMidNum(rangeTime);
	}

	public double getKNNTime() {
		// TODO Auto-generated method stub
		return getAvg(knnTime);
//		return getMidNum(knnTime);
	}

	private double getMidNum(List<Long> list) {
		if (list.size() == 0)
			return 0;
		Collections.sort(list);
		if ((list.size() & 1) == 0)
			return (list.get(list.size()/2)+list.get(list.size()/2+1))/2;
		else
			return list.get(list.size()/2);
	}
	
	private double getAvg(List<Long> list) {
		if (list.size() == 0)
			return 0;
		Long time = 0L;
		for (Long t : list)
			time += t;
		return (time+0.0)/list.size();
	}
}
