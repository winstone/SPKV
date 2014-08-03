package cn.sdp.pkv.store.model;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import cn.sdp.pkv.data.type.OperateType;

public class BTreeOperator implements Comparable<BTreeOperator> {
	private OperateType type;
	private String key;
	private String key2;
	private ValueObject obj;
	private BlockingQueue<BTreeOperateResult> ret;
	private BTreeOperateResult res;
//	private boolean isSet = false;

	public BTreeOperator(OperateType type, String key, ValueObject obj) {
		super();
		this.type = type;
		this.key = key;
		this.key2 = null;
		this.obj = obj;
		this.ret = new ArrayBlockingQueue<BTreeOperateResult>(1);
	}

	public BTreeOperator(OperateType type, String key, String key2) {
		super();
		this.type = type;
		this.key = key;
		this.key2 = key2;
		this.obj = null;
		this.ret = new ArrayBlockingQueue<BTreeOperateResult>(1);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ValueObject getObj() {
		return obj;
	}
	public void setObj(ValueObject obj) {
		this.obj = obj;
	}
	public void setType(OperateType type) {
		this.type = type;
	}
	public OperateType getType() {
		return type;
	}
	public String getKey2() {
		return key2;
	}
	
	public void setOperateResult(BTreeOperateResult res)
	{
		try {
			ret.put(res);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		this.res = res;
//		this.isSet = true;
//		this.notify();
	}
	
	public int getIntResult()
	{
//		while (!isSet)
//		{
//			try {
//				this.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if (res != null)
//			return res.getRet();
//		else
//			return -2;
		BTreeOperateResult res;
		try {
			res = ret.take();
			return res.getRet();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
	}

	public ConcurrentNavigableMap<String, ValueObject> getObjResult()
	{
//		while (!isSet)
//		{
//			try {
//				this.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if (res != null)
//			return res.getObjRets();
//		else
//			return new ConcurrentSkipListMap<String, ValueObject>();
		BTreeOperateResult res;
		try {
			res = ret.take();
			return res.getObjRets(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ConcurrentSkipListMap<String, ValueObject>();
		}
	}
	
	@Override
	public int compareTo(BTreeOperator op) {
		return this.key.compareTo(op.getKey());
	}
}
