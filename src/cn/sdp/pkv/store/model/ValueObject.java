package cn.sdp.pkv.store.model;

import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;


//public class ValueObject implements Serializable {
//	private static final long serialVersionUID = 6834338743132962140L;
//	private Object[] cols;
////	private byte[] content;
//	private String content;
//	private long timestamp;
//
//	public ValueObject(Object[] cols, String content, long ts) {
//		super();
//		this.cols = cols;
//		setContent(content);
//		this.timestamp = ts;
//	}
//	
//	public void setCols(Object[] cols) {
//		this.cols = cols;
//	}
//	public Object[] getCols() {
//		return cols;
//	}
//	public void setTimestamp(long timestamp) {
//		this.timestamp = timestamp;
//	}
//	public long getTimestamp() {
//		return timestamp;
//	}
//	public void setContent(String content) {
////		this.content = SnappyUtil.compress(content);
//		this.content = content;
//	}
//	public String getContent() {
////		return SnappyUtil.uncompress(this.content);
//		return this.content;
//	}
//}

public class ValueObject {
	private SerialObj.SerialObject sObj;
	
	public ValueObject(List<Integer> cols, List<String> list, long ts) {
		super();
		SerialObj.SerialObject.Builder builder = SerialObj.SerialObject.newBuilder();
		builder.addAllCols(cols);
		builder.addAllContents(list);
		builder.setTimestamp(ts);
		sObj = builder.build();
	}
	
	public ValueObject(byte[] data) {
		try {
			sObj = SerialObj.SerialObject.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Integer> getCols() {
		return sObj.getColsList();
	}
	public long getTimestamp() {
		return sObj.getTimestamp();
	}
	public List<String> getContentList() {
		return sObj.getContentsList();
	}
	public byte[] toBytes() {
		return sObj.toByteArray();
	}
}