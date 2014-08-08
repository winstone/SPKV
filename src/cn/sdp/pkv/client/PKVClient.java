package cn.sdp.pkv.client;

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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.PKVService;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;

public class PKVClient implements Runnable {
	private int dimension = 15;
	private static List<Integer> lb = Arrays.asList(1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000);
	private static List<Integer> ub = Arrays.asList(2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000);
//	private List<Integer> lb = Arrays.asList(0, 0, 0, 0, 0);
//	private List<Integer> ub = Arrays.asList(60, 500, 2600, 2600, 2600);
	private List<String> indexCols = Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6",
												   "col7", "col8", "col9", "col10", "col11", "col12",
												   "col13", "col14", "col15");
	private List<String> contents = Arrays.asList("content");
	private int lineLength = 24+8*dimension;
	private final String tableName = "pkv";
	private final int EXECUTE_THRESHOLD = Configs.EXECUTE_THRESHOLD;
	private final String FILE_PATH = Configs.FILE_PATH;
	private int interval = 24*60*60*1000;
	private SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");

	private List<String> hosts = Arrays.asList("219.224.171.143");
//	private List<String> hosts = Arrays.asList("219.224.171.142","219.224.171.141","219.224.171.140","219.224.171.139",
//			"219.224.171.138","219.224.171.137","219.224.171.136","219.224.171.135","219.224.171.134","219.224.171.133");
	private final List<PKVService.Client> clients;
	private final List<TTransport> trans;
	private final int from;
	private final int to;
	private final int offset;
	private final Random ran;
	private List<SPKVObject> testobjs;
	
	public PKVClient(int f, int t, int offset, boolean isReal)
	{
		this.from = f;
		this.to = t;
		this.offset = offset;
		this.ran = new Random();
		clients = new ArrayList<PKVService.Client>();
		trans = new ArrayList<TTransport>();
		getClientList();
		if (isReal)
			testobjs = inputRealSPKVObject1(from, to);
		else
			testobjs = inputSPKVObject(from, to);
		System.out.println("Loaded"); 
	}
	
	public void run() { 
		
		try {

//			long t1 = System.currentTimeMillis();
//			System.out.println("insert:"+from+"-"+to+"-"+offset);
//			insertData();
//			long t2 = System.currentTimeMillis();
//			System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			Scanner scan = new Scanner(System.in); 
			int choose = 0;
			while (choose >= 0)
			{
				System.out.print("Input Choose:");
				choose = scan.nextInt();
				long t1 = System.currentTimeMillis();
				switch (choose)
				{
				case 0:
				case 1:
				case 2:
				case 5:
				case 6:
				case 7:
					testQuerys(testobjs, choose);
					break;
				case 3:
					insertData();
					break;
				case 4:
					System.out.print("Create Table...");
					clients.get(ran.nextInt(clients.size())).createIndexTable(
							new IndexInfo(tableName, lb, ub, dimension, indexCols, contents));
					System.out.println("OK");
					break;
				default:
					choose = -1;
					break;
				}
				long t2 = System.currentTimeMillis();
				System.out.println("Total time: "+String.format("Time:%.3f",(t2-t1)/1000.0));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (TTransport tran : trans)
			tran.close();
    }

	private void getClientList()
	{
		for (String host : hosts)
		{
			PKVService.Client cl = getClient(host);
			if (cl != null)
				clients.add(cl);
		}
	}

	private PKVService.Client getClient(String dbIP)
	{
		TSocket socket = new TSocket(dbIP, Configs.INDEX_PORT);
		socket.setTimeout(60000);
		TTransport transport = new TFramedTransport(socket);   
		TProtocol proto = new TBinaryProtocol(transport);
		PKVService.Client cl = new PKVService.Client(proto);
		try {
			transport.open();
		} catch (TTransportException e) {
			
			e.printStackTrace();
		}
		if (transport.isOpen())
		{
			trans.add(transport); 
			
			return cl;
		}
		else
			return null;
	}
	
	public void insertData()
	{
		System.out.print("Insert Data...");
		int ret = 0;
		int sindex = 0; 
		int eindex = EXECUTE_THRESHOLD;
		while (sindex <= testobjs.size())
		{
			Map<String, List<SPKVObject>> objs = new HashMap<String, List<SPKVObject>>();
			eindex = eindex>testobjs.size()?testobjs.size():eindex;
			List<SPKVObject> subList = testobjs.subList(sindex, eindex);
			objs.put(tableName, subList);
			try {
				ret += clients.get(ran.nextInt(clients.size())).batchInsertObjects(objs);
			} catch (TException e) {
				
				e.printStackTrace();
				System.out.println("ERROR "+sindex);
				return;
			}
			if (ret != 0)
			{
				System.out.println("ERROR "+sindex);
				return;
			}
			sindex += EXECUTE_THRESHOLD;
			eindex += EXECUTE_THRESHOLD;
		}
		System.out.println("OK");
	}

	private void testQuerys(List<SPKVObject> testobjs, int type) throws InterruptedException, TException {
		for (int i = 0;i < 8;i++)
		{
			long t1 = System.currentTimeMillis();
			if (type == 0)
			{
				System.out.println("Point Query");
				pyramidPointQuery(testobjs);
			}
			else if (type == 1)
			{
				System.out.println("Range Query");
				pyramidRangeQuery(testobjs, 266000);
			}
			else if (type == 2)
			{
				System.out.println("KNN Query");
				pyramidKNNQuery(testobjs, 50);
			}
			else if (type == 5)
			{
				System.out.println("Query 1");
				Query1();
			}
			else if (type == 6)
			{
				System.out.println("Query 2 Point");
				Query2Point(testobjs);
			}
			else if (type == 7)
			{
				System.out.println("Query 2 kNN");
				Query2KNN(testobjs, 10);
			}
			long t2 = System.currentTimeMillis();
			System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			Thread.sleep(500);
		}
	}

	private void Query2KNN(List<SPKVObject> objs, int K) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
//			System.out.println("Query:"+Arrays.toString(v));
			//KNN²éÑ¯
			List<SPKVObject> res = clients.get(ran.nextInt(clients.size())).knnQuery(tableName, obj.getCols(), K, contents);
//			for (SPKVObject r : res)
//				System.out.println(r.getContent());
			s += res.size();
			if (res.size() == 0)
				zero++;
		} 
		System.out.println(s);
		System.out.println(zero);
	}

	private void Query2Point(List<SPKVObject> objs) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 100;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			//µã²éÑ¯Count
			int t = clients.get(ran.nextInt(clients.size())).pointQuery(tableName, obj.getCols(), contents).size();
			s += t;
			if (t == 0)
				zero++;
		}
		System.out.println(s);
		System.out.println(zero);
	}

	private void Query1() throws TException {
		Random ran = new Random();
		int quantity = ran.nextInt(10)+15;
		int dis = ran.nextInt(500);
		int sd = ran.nextInt(1500);
		int s = 0;
		for (int i = 0;i < 10;i++)
		{
			List<SPColumn> ql = new ArrayList<SPColumn>();
			List<SPColumn> qu = new ArrayList<SPColumn>();

			ql.add(new SPColumn("Quantity", 0));
			qu.add(new SPColumn("Quantity", quantity));
			
			ql.add(new SPColumn("Discount", dis));
			qu.add(new SPColumn("Discount", dis+100));
			
			ql.add(new SPColumn("Shipdate", sd));
			qu.add(new SPColumn("Shipdate", sd+365));

			ql.add(new SPColumn("Commitdate", sd));
			qu.add(new SPColumn("Commitdate", sd+365));

			ql.add(new SPColumn("Receiptpdate", sd));
			qu.add(new SPColumn("Receiptpdate", sd+365));
			//·¶Î§²éÑ¯
//			List<SPKVObject> ret = iMan.RangeQuery(ql, qu);
//			for (SPKVObject r : ret)
//				System.out.println(Arrays.toString(r.getCols()));
//			System.out.println("--------");
			//·¶Î§²éÑ¯Count
			int ret = clients.get(ran.nextInt(clients.size())).rangeQueryCount(tableName, ql, qu);
			s += ret;
		}
		System.out.println(s);
		
	}

	public void pyramidRangeQuery(List<SPKVObject> objs, int dist) throws TException {
		Random ran = new Random();
		int s = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			
			List<SPColumn> ql = new ArrayList<SPColumn>();
			List<SPColumn> qu = new ArrayList<SPColumn>();
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
				ql.add(new SPColumn("col"+(j+1), colL));
				qu.add(new SPColumn("col"+(j+1), colU));
			}
			//·¶Î§²éÑ¯
//			System.out.println("L:"+Arrays.toString(ql));
//			System.out.println("U:"+Arrays.toString(qu));
//			List<SPKVObject> ret = iMan.RangeQuery(ql, qu);
//			for (SPKVObject r : ret)
//				System.out.println(Arrays.toString(r.getCols()));
//			System.out.println("--------");
			//·¶Î§²éÑ¯Count
			int ret = clients.get(ran.nextInt(clients.size())).rangeQueryCount(tableName, ql, qu);
			s += ret;
		}
		System.out.println(s);
	}
	
	public void pyramidKNNQuery(List<SPKVObject> objs, int K) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
//			System.out.println("Query:"+Arrays.toString(v));
			//KNN²éÑ¯
			List<SPKVObject> res = clients.get(ran.nextInt(clients.size())).knnQuery(tableName, obj.getCols(), K, contents);
//			for (SPKVObject r : res)
//				System.out.println(r.getContent());
			s += res.size();
			if (res.size() == 0)
				zero++;
		} 
		System.out.println(s);
		System.out.println(zero);
	}
	
	/**
	 * Query data items from the inserted data items with specified values.
	 * The specified values are random picked from the inserted data items.
	 * 
	 * @param objs inserted data items
	 * @throws TException
	 */
	public void pyramidPointQuery(List<SPKVObject> objs) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			PKVService.Client client = clients.get(ran.nextInt(clients.size()));
			List<SPColumn> colsList = obj.getCols();
			for(SPColumn col: colsList){
				System.out.print(col.getValue()+" ");
			}
			System.out.println();
			//µã²éÑ¯Count
			int t = client.pointQuery(tableName, obj.getCols(), contents).size();
			s += t;
			if (t == 0)
				zero++;
		}
		System.out.println("Found "+s);
		System.out.println(zero);
	}
	
	/**
	 * Read the data from data file.
	 * @param start
	 * @param end
	 * @return
	 */
	public List<SPKVObject> inputSPKVObject(int start, int end) {
		String fileName = FILE_PATH+dimension+"\\"+tableName+"-"+dimension+"-"+String.format("%02d", offset)+".dat";
//		String fileName = FILE_PATH+tableName+"-"+dimension+"-"+String.format("%02d", offset)+".dat";
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
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return objects;
	}
	
	public List<SPKVObject> inputRealSPKVObject(int start, int end, boolean rewrite) {
		Random ran = new Random();
		String fileName = FILE_PATH+"lineitem.tbl."+offset;
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			int offset = start;
			long baseTime = dfs.parse("1992-1-1").getTime();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			BufferedWriter out = null;
			if (rewrite)
				out = new BufferedWriter(new FileWriter(fileName+".r", true));
			for (int i = 0;i < end;i++)
			{
				String line = in.readLine();
				if (line == null)
					break;
				if (i >= offset)
				{
					String[] cols = line.split("\\|");
					int quantity = Integer.parseInt(cols[4]);
					int discount = 0;
					if (rewrite)
						discount = ran.nextInt(500);
					else
						discount = Integer.parseInt(cols[6]);
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
					if (rewrite)
					{
						String newline = getNewLine(cols, discount);
						out.append(newline);
					}
				}
			}
			in.close();
			if (rewrite)
				out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return objects;
	}

	private String getNewLine(String[] cols, int discount) {
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

	private int getDateDiff(int interval, SimpleDateFormat dfs, long baseTime, String col)
			throws ParseException {
		long lShipdate = (dfs.parse(col).getTime()-baseTime) / interval;
		return Integer.parseInt(lShipdate+"");
	}
}
