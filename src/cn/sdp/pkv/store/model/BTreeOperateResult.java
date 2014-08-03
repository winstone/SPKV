package cn.sdp.pkv.store.model;

import java.util.concurrent.ConcurrentNavigableMap;

public class BTreeOperateResult {
	ConcurrentNavigableMap<String, ValueObject> objRets;
	private int ret;
	
	public ConcurrentNavigableMap<String, ValueObject> getObjRets() {
		return objRets;
	}
	public void setObjRets(ConcurrentNavigableMap<String, ValueObject> objRets) {
		this.objRets = objRets;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	
}
