package cn.sdp.pkv.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.sdp.pkv.thrift.SPKVObject;

public class TPCTest {
	private final static String FILE_PATH = "E:\\PkvFile\\";
	private static int interval = 24*60*60*1000;
	private static SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		
		inputSPKVObject(3);
		long t2 = System.currentTimeMillis();
		System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
	
	}
	
	public static List<SPKVObject> inputSPKVObject(int offset) {
		Random ran = new Random();
		String fileName = FILE_PATH+"lineitem.tbl."+offset;
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			long baseTime = dfs.parse("1992-1-1").getTime();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line = in.readLine();
			int i = 0;
			while (line != null)
			{
//				String[] cols = line.split("\\|");
//				int quantity = Integer.parseInt(cols[4]);
//				int discount = ran.nextInt(500);
//				int shipdate = getDateDiff(interval, dfs, baseTime, cols[10]);
//				int commitdate = getDateDiff(interval, dfs, baseTime, cols[11]);
//				int receiptpdate = getDateDiff(interval, dfs, baseTime, cols[12]);
//				String key = cols[0]+":"+cols[3];
//				String content = getContent(cols);
//				SPKVObject obj = new SPKVObject(key, Arrays.asList(
//						quantity,discount,shipdate,commitdate,receiptpdate), content);
//				objects.add(obj);
				i++;
				line = in.readLine();
			}
			System.out.println(i);
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objects;
	}

	private static String getContent(String[] cols) {
		StringBuilder sb = new StringBuilder();
		sb.append(cols[1]).append(":");
		sb.append(cols[2]).append(":");
		sb.append(cols[5]).append(":");
		sb.append(cols[7]).append(":");
		sb.append(cols[8]).append(":");
		sb.append(cols[9]).append(":");
		sb.append(cols[13]).append(":");
		sb.append(cols[14]).append(":");
		sb.append(cols[15]);
		return sb.toString();
	}

	private static int getDateDiff(int interval, SimpleDateFormat dfs, long baseTime, String col)
			throws ParseException {
		long lShipdate = (dfs.parse(col).getTime()-baseTime) / interval;
		return Integer.parseInt(lShipdate+"");
	}
}
