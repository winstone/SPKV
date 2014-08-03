package cn.sdp.pkv.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class PKVConverter {


	public static int getIntegerIP(String strIP) {
		String[] ipBytes = strIP.split("\\.");
		int res = 0;
		int b = Integer.parseInt(ipBytes[0]);
		res |= (b << 24);
		b = Integer.parseInt(ipBytes[1]);
		res |= (b << 16);
		b = Integer.parseInt(ipBytes[2]);
		res |= (b << 8);
		b = Integer.parseInt(ipBytes[3]);
		res |= b;
		
		return res;
	}
	
	public static String getStringIP(int intIP)
	{
		StringBuilder sb = new StringBuilder();
		sb.append((intIP & 0xff000000)>>>24);
		sb.append(".");
		sb.append((intIP & 0x00ff0000)>>>16);
		sb.append(".");
		sb.append((intIP & 0x0000ff00)>>>8);
		sb.append(".");
		sb.append((intIP & 0x000000ff));
		return sb.toString();
	}
	
	public static String[] objsToStrings(Object[] objs)
	{
		String[] cols = new String[objs.length];
		for (int i = 0;i < objs.length;i++)
		{
			Array.set(cols, i, String.valueOf(objs[i]));
		}
		return cols;
	}

	public static Object[] stringsToObjs(String[] cols)
	{
		Object[] objs = new Object[cols.length];
		for (int i = 0;i < cols.length;i++)
		{
			Array.set(objs, i, Integer.parseInt(cols[i]));
		}
		return objs;
	}
		
	public static ByteBuffer toByteBuffer(String value) 
	{
		try {
			return ByteBuffer.wrap(value.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String toString(ByteBuffer buffer) 
	{
	    try {
			byte[] bytes = new byte[buffer.remaining()];
		    buffer.get(bytes);
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
