package cn.sdp.pkv.dht;

import cn.sdp.pkv.util.PKVConverter;

public class EndPoint implements Comparable<EndPoint>{
	private long token;
	private int integerIP;
	
	public EndPoint(long token, String strIP) {
		super();
		this.token = token;
		this.integerIP = PKVConverter.getIntegerIP(strIP);
	}
	
	public void setIntegerIP(int integerIP) {
		this.integerIP = integerIP;
	}
	public int getIntegerIP() {
		return integerIP;
	}	
	public void setToken(long token) {
		this.token = token;
	}
	public Long getToken() {
		return token;
	}
	
	public String getStringIP() {
		// TODO Auto-generated method stub
		return PKVConverter.getStringIP(integerIP);
	}
	
	@Override
	public int compareTo(EndPoint o) {
		// TODO Auto-generated method stub
		return ((Long)token).compareTo(o.getToken());
	}
}
