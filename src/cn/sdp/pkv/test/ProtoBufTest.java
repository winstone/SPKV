package cn.sdp.pkv.test;

import java.io.UnsupportedEncodingException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.sdp.pkv.store.model.SerialObj;
import cn.sdp.pkv.thrift.SPKVRow;

public class ProtoBufTest {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidProtocolBufferException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, InvalidProtocolBufferException {
		// TODO Auto-generated method stub
//		SerialObj.SerialObject.Builder builder = SerialObj.SerialObject.newBuilder();
//		for (int i = 0;i < 6;++i)
//			builder.addCols(2);
//		builder.addContents("在命令行利用protoc 工具生成builder类");
//		builder.setTimestamp(2334234L);
//		
//		SerialObj.SerialObject obj = builder.build();
//		
////		String serial = new String(obj.toByteArray(), "ISO-8859-1");
//		ByteString serial = obj.toByteString();
//		SerialObj.SerialObject obj1 = SerialObj.SerialObject.parseFrom(serial);
//		System.out.println(obj1.getContents(0));
//		System.out.println(obj1.getTimestamp());
//		for (int i = 0;i < obj.getColsCount();++i)
//			System.out.print(obj.getCols(i)+" ");
		
	}

}
