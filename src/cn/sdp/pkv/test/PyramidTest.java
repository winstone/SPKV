package cn.sdp.pkv.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import cn.sdp.pkv.data.PKVManager;
import cn.sdp.pkv.data.TableManager;
import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.pyramid.PyramidIndexTable;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;
import cn.sdp.pkv.util.Configs;

public class PyramidTest {
	private static int dimension = 15;
	private static List<Integer> lb = Arrays.asList(1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000);
	private static List<Integer> ub = Arrays.asList(2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000);
//	private static Object[] lb = {100, 100, 100, 100, 100, 100};
//	private static Object[] ub = {600, 600, 600, 600, 600, 600};
	private static List<String> indexCols = Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6",
			   "col7", "col8", "col9", "col10", "col11", "col12",
			   "col13", "col14", "col15", "col16", "col17", "col18");
	private static List<String> contents = Arrays.asList("content");
	private static int lineLength = 24+8*dimension;
	private final static String tableName = "pkv";

	private final static String FILE_PATH = Configs.FILE_PATH;
	private static PKVManager pMan = PKVManager.getInstance();

	public static void main(String[] args) throws InterruptedException {
		
//		System.out.println(Configs.PEER_TIMEOUT);
//		List<SPKVObject> testobjs = inputSPKVObject(00000, 10000, 0);
//		System.out.println("Loaded");  
		long t1 = System.currentTimeMillis();
		for (int i = 0;i < 1;i++)
			generateSPKVObjects(5, 5*i);
		
//		generateRealPartObject();
		//建立新表
//		pMan.createTable(new TableInfo(tableName, lb, ub, dimension, true, indexCols, contents));
		//插入数据
//		pMan.insertObjects(tableName, testobjs, t1);
//		for (SPKVObject obj : testobjs)
//			obj.setContent("");
//		List<SPKVObject> objs = CassandraDAO.getInstance().getContent(tableName, testobjs);
//		for (SPKVObject obj : objs)
//			System.out.println(obj.getContent());
//		testQuerys(testobjs, 2);
//		pyramidRangeQuery(testobjs, 200000);
		long t2 = System.currentTimeMillis();
		System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		pMan.shutDown();
	}
	
	public static void generateRealPartObject() {
		int interval = 24*60*60*1000;
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = FILE_PATH+"lineitem.tbl.1";
		try {
			long baseTime = dfs.parse("1992-1-1").getTime();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			
			for (int j = 0;j < 15;j++)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName+"."+j));
				for (int i = 0;i < 100000;i++)
				{
					String line = in.readLine();
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
					String newline = getNewLine(cols, discount);
					out.append(newline);
					
				}
				out.close();
			}
			in.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}
	
	private static void testQuerys(List<SPKVObject> testobjs, int type) throws InterruptedException {
		for (int i = 0;i < 8;i++)
		{
			long t1 = System.currentTimeMillis();
			if (type == 0)
				pyramidPointQuery(testobjs);
			else if (type == 1)
				pyramidRangeQuery(testobjs, 200000);
			else
				pyramidKNNQuery(testobjs, 10);
			long t2 = System.currentTimeMillis();
			System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			Thread.sleep(500);
		}
	}


	private static String getNewLine(String[] cols, int discount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0;i < cols.length;i++)
		{
			if (i > 0)
				sb.append("|");
			if (i == 6)
				sb.append(discount);
			else
				sb.append(cols[i]);
		}
		sb.append("\n");
		return sb.toString();
	}

	private static List<SPContent> getContent(String[] cols) {
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

	private static int getDateDiff(int interval, SimpleDateFormat dfs, long baseTime, String col)
			throws ParseException {
		long lShipdate = (dfs.parse(col).getTime()-baseTime) / interval;
		return Integer.parseInt(lShipdate+"");
	}
	
	public static void pyramidRangeQuery(List<SPKVObject> objs, int dist) throws InterruptedException {
		Random ran = new Random();
		int s = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			
			Object[] ql = new Object[dimension];
			Object[] qu = new Object[dimension];
			List<SPColumn> cols = obj.getCols();
//			int a = Integer.parseInt(cols[0]);
//			int b = Integer.parseInt(cols[1]);
//			ql[0] = a-110<100?100:a-110;
//			qu[0] = a+110>500?500:a+110;
//			ql[1] = b-110<100?100:b-110;
//			qu[1] = b+110>500?500:b+110;
			for (int j = 0;j < dimension;j++)
			{
				int col = cols.get(j).getValue();
				int colL = col-dist<lb.get(j)?lb.get(j):col-dist;
				int colU = col+dist>ub.get(j)?ub.get(j):col+dist;
				Array.set(ql, j, colL);
				Array.set(qu, j, colU);
			}
			//范围查询
//			System.out.println("L:"+Arrays.toString(ql));
//			System.out.println("U:"+Arrays.toString(qu));
//			List<SPKVObject> ret = iMan.RangeQuery(ql, qu);
//			for (SPKVObject r : ret)
//				System.out.println(Arrays.toString(r.getCols()));
//			System.out.println("--------");
			//范围查询Count
			int ret = pMan.RangeQueryCount(tableName, ql, qu);
			s += ret;
		}
		System.out.println(s);
	}
	
	public static void pyramidKNNQuery(List<SPKVObject> objs, int K) {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
//			System.out.println("Query:"+Arrays.toString(v));
			//KNN查询
			List<Integer> retCols = new ArrayList<Integer>();
			retCols.add(0);
			List<SPKVRow> res = pMan.KNNQuery(tableName, obj.getCols().toArray(), K, retCols);
			s += res.size();
			if (res.size() == 0)
				zero++;
		} 
		System.out.println(s);
		System.out.println(zero);
	}
	
	public static void pyramidPointQuery(List<SPKVObject> objs) {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 1000;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			//点查询Count
			List<Integer> qv = new ArrayList<Integer>();
			for (SPColumn col : obj.getCols())
				qv.add(col.getValue());
			int t = pMan.PointQueryCount(tableName, qv.toArray());
			s += t;
			if (t == 0)
				zero++;
		}
		System.out.println(s);
		System.out.println(zero);
	}
	
	public static void generateStatics(String fileName, int start, int end)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(fileName));
			PrintWriter out = new PrintWriter(
					new FileOutputStream(fileName.replace("test", "stat").replace("txt", "csv")));
			int index = 1+dimension;
			int offset = start;
			for (int i = 0;i < end;i++)
			{
				byte[] buf = new byte[dimension];
				in.readFully(buf);
				if (i >= offset)
				{
					String line = new String(buf, "utf-8");
					String[] cols = line.split(",");
					if (map.containsKey(cols[index]))
					{
						map.put(cols[index], map.get(cols[index])+1);
					}
					else
					{
						map.put(cols[index], 1);
					}
				}
			}
			in.close();
			for (String key : map.keySet())
			{
				out.write(key+","+map.get(key)+"\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	

	public static List<SPKVObject> inputSPKVObject(int start, int end, int file) {
		String fileName = FILE_PATH+dimension+"\\"+tableName+"-"+dimension+"-"+String.format("%02d", file)+".dat";
//		String fileName = FILE_PATH+tableName+"-"+dimension+"-"+file+".dat";
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
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return objects;
	}

	/**
	 * Generate random data and store the generated data into a file. The
	 * generated data should be not overlapped. So the offset begins from 0, and
	 * after each generating, the offset should plus the size variable.
	 * 
	 * @param size
	 *            data size
	 * @param offset
	 *            generated data's offset
	 */
	public static void generateSPKVObjects(int size, int offset)
	{
		int fileOffset = offset / size;
		String fileName = FILE_PATH+dimension+"\\"+tableName+"-"+dimension+"-"+String.format("%02d", fileOffset)+".dat";
		Random ran = new Random();
		String strkey = "";
		String content = "";
		StringBuilder sb = new StringBuilder();
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
			for (int i = 0;i < size;i++)
			{
				int key = i+offset;
				strkey = String.format("%08d", key);
				Object[] v = new Object[dimension];
				content = "content"+String.format("%08d", key);
	
				sb.delete(0, sb.length());
				sb.append(strkey);
				sb.append(',');
				for (int j = 0;j < dimension;j++)
				{
					int col = ran.nextInt(ub.get(j)-lb.get(j))+lb.get(j);
					Array.set(v, j, col);
					sb.append(col+",");
				}
				sb.append(content);
				
				System.out.println("Insert data:"+sb.toString());
				out.write(sb.toString().getBytes("utf-8"));
			}
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(fileName+" created");
	}

	private static String getMD5(String str)
	{
		byte[] md5byte = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(str.getBytes("utf-8"));
			md5byte = md5.digest();
		
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		return byteToString(md5byte);
	}

	private static String byteToString(byte[] buf)
	{
		String result = "";

		for (int i = 0;i < buf.length; i++)
		{
			result += Integer.toString((buf[i]&0xff)+0x100,16).substring(1).toUpperCase();
		}
		return result;
	}
}
