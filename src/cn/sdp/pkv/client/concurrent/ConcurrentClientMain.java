package cn.sdp.pkv.client.concurrent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;

public class ConcurrentClientMain {
	private final static int threadCount = 5;
	private final static int from = 00000;
	private final static int to = 10000;
	private final static int offset = 21;
	private final static int opCount = 500;
	private static int dimension = 6;
	private static int lineLength = 26+8*dimension;
	private final static CompletionService<TestResult> testServ = new ExecutorCompletionService<TestResult>(
			Executors.newFixedThreadPool(threadCount));

	private final static String FILE_PATH = Configs.FILE_PATH;
	private static int interval = 24*60*60*1000;
	private static SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static void main(String[] args) {
//		List<SPKVObject> objs = inputRealSPKVObject1(from, to);
		List<SPKVObject> objs = inputSPKVObject(from, to);
		System.out.println("Loaded");

		long t1 = System.currentTimeMillis();
		for (int i = 0;i < threadCount;++i)
			testServ.submit(new ConcurrentClient(objs, opCount/threadCount));
		TestResult res = new TestResult();
		for (int i = 0; i < threadCount;i++) 
		{
			try {
				Future<TestResult> ret = testServ.take();
				
				res.add(ret.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println(String.format("Time:%.3f", (t2-t1)/1000.0));
		
		System.out.println(String.format("Update: %d", res.updateTime.size()));
		System.out.println(String.format("Range: %d", res.rangeSize));
		System.out.println(String.format("Update Time: %.3f", res.getUpdateTime()));
		System.out.println(String.format("Point Query Time: %.3f", res.getPointTime()));
		System.out.println(String.format("Range Query Time: %.3f", res.getRangeTime()));
		System.out.println(String.format("KNN Query Time: %.3f", res.getKNNTime()));
	}

	public static List<SPKVObject> inputSPKVObject(int start, int end) {
		String fileName = FILE_PATH+dimension+"\\pkv-6-"+String.format("%02d", offset)+".dat";
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(fileName));
			int offset = start;
			for (int i = 0;i < end;i++)
			{
				byte[] buf = new byte[lineLength];
				in.readFully(buf);
				if (i >= offset)
				{
					String line = new String(buf, "utf-8");
					String[] cols = line.split(",");

					SPKVObject obj = new SPKVObject();
					obj.setKey(cols[0]);
					for (int j = 0;j < dimension;j++)
						obj.addToCols(new SPColumn("col"+(j+1), Integer.parseInt(cols[j+1]))); 
					obj.addToContent(new SPContent("content", cols[cols.length-1]));
					
					objects.add(obj);
				}
			}
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
		}
		return objects;
	}
	
	public List<SPKVObject> inputRealSPKVObject1(int start, int end) {
		String fileName = FILE_PATH+"lineitem.tbl."+offset;
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			int offset = start;
			long baseTime = dfs.parse("1992-1-1").getTime();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			for (int i = 0;i < end;i++)
			{
				String line = in.readLine();
				if (line == null)
					break;
				if (i >= offset)
				{
					String[] cols = line.split("\\|");
					int quantity = Integer.parseInt(cols[4]);
					int discount = Integer.parseInt(cols[6]);
					int shipdate = getDateDiff(interval, dfs, baseTime, cols[10]);
					int commitdate = getDateDiff(interval, dfs, baseTime, cols[11]);
					int receiptpdate = getDateDiff(interval, dfs, baseTime, cols[12]);
					String key = cols[0]+":"+cols[3];
					List<SPContent> content = getContent(cols);
					SPKVObject obj = new SPKVObject();
					obj.setKey(key);
					obj.addToCols(new SPColumn("Quantity", quantity));
					obj.addToCols(new SPColumn("Discount", discount));
					obj.addToCols(new SPColumn("Shipdate", shipdate));
					obj.addToCols(new SPColumn("Commitdate", commitdate));
					obj.addToCols(new SPColumn("Receiptpdate", receiptpdate));
					obj.setContent(content);
					objects.add(obj);
				}
			}
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

	private static int getDateDiff(int interval, SimpleDateFormat dfs, long baseTime, String col)
			throws ParseException {
		long lShipdate = (dfs.parse(col).getTime()-baseTime) / interval;
		return Integer.parseInt(lShipdate+"");
	}

	private List<SPContent> getContent(String[] cols) {
		List<SPContent> contents = new ArrayList<SPContent>();
//		sb.append(cols[1]).append(":");
//		sb.append(cols[2]).append(":");
//		sb.append(cols[5]).append(":");
//		sb.append(cols[7]);
//		sb.append(cols[7]).append(":");
//		sb.append(cols[8]).append(":");
//		sb.append(cols[9]).append(":");
//		sb.append(cols[13]).append(":");
//		sb.append(cols[14]).append(":");
//		sb.append(cols[15]);
		contents.add(new SPContent("Col5", cols[5]));
		contents.add(new SPContent("Col7", cols[7]));
		return contents;
	}
}
