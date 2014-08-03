package cn.sdp.pkv.client.concurrent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.store.dao.HectorObjectDAO;
import cn.sdp.pkv.thrift.PKVService;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;


public class PKVClient {
	private static int dimension = 6;
	private static List<Integer> lb = Arrays.asList(1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000);
	private static List<Integer> ub = Arrays.asList(2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000, 2000000);
	static int lineLength = 24+8*dimension;
	private List<String> indexCols = Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6");
	private List<String> contents = Arrays.asList("content");
	private final String tableName = "pkv";
	private final int EXECUTE_THRESHOLD = Configs.EXECUTE_THRESHOLD;
	private final static String FILE_PATH = Configs.FILE_PATH;
	private static int interval = 24*60*60*1000;
	private static SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");


	private List<String> hosts = Arrays.asList("219.224.171.142","219.224.171.141","219.224.171.140","219.224.171.139",
			"219.224.171.138","219.224.171.137","219.224.171.136","219.224.171.135","219.224.171.134","219.224.171.133");
//	private List<String> hosts = Arrays.asList("192.168.3.223");
	private List<PKVService.Client> clients;
	private List<TTransport> trans;
	private List<SPKVObject> testobjs;
	
	private int from;
	private int to;
	private int offset;
	private Random ran;
	private HectorObjectDAO dao;
		
	public PKVClient(int f, int t, int offset, boolean real)
	{
		this.from = f;
		this.to = t;
		this.offset = offset;
		this.ran = new Random();
		clients = new ArrayList<PKVService.Client>();
		trans = new ArrayList<TTransport>();
		
		getClientList();
		System.out.println("Connected"); 

		if (real)
			testobjs = inputRealSPKVObject1(from, to);
		else
			testobjs = inputSPKVObject(from, to);
		System.out.println("Loaded"); 
	}

	public PKVClient(List<SPKVObject> objs)
	{
		this.ran = new Random();
		clients = new ArrayList<PKVService.Client>();
		trans = new ArrayList<TTransport>();
		
		getClientList();
		System.out.println("Connected"); 
		testobjs = objs;
	}
	
	public void insertSPKV() { 
		
		long t1 = System.currentTimeMillis();
		System.out.println("insert:"+from+"-"+to+"-"+offset);
		batchInsertObject(testobjs, true);
		long t2 = System.currentTimeMillis();
		System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
		
		
		for (TTransport tran : trans)
			tran.close();
    }

	public void insertCassandra(boolean isReal) { 
//		dao = HectorObjectDAO.getInstance();
		
		try {
			if (isReal)
			{
//				List<LineItem> testobjs = inputRealSPKVObject(from, to, false);
//				long t1 = System.currentTimeMillis();
//				System.out.println("insert:"+from+"-"+to+"-"+offset);
//				insertLineItem(testobjs);
//				long t2 = System.currentTimeMillis();
//				System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			}
			else
			{
				List<SPKVObject> testobjs = inputSPKVObject(from, to);
				System.out.println("Loaded"); 
				long t1 = System.currentTimeMillis();
				System.out.println("insert:"+from+"-"+to+"-"+offset);
				batchInsertObject(testobjs, false);
				long t2 = System.currentTimeMillis();
				System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (TTransport tran : trans)
			tran.close();
    }
	
//	private void insertLineItem(List<LineItem> testobjs) {
//
//		System.out.print("Insert Data...");
//		int ret = 0;
//		int sindex = 0; 
//		int eindex = EXECUTE_THRESHOLD;
//		while (sindex <= testobjs.size())
//		{
//			eindex = eindex>testobjs.size()?testobjs.size():eindex;
//			List<LineItem> subList = testobjs.subList(sindex, eindex);
//			ret += dao.insertRealObject(tableName, subList);
//			
//			if (ret != 0)
//			{
//				System.out.println("ERROR "+sindex);
//				return;
//			}
//			sindex += EXECUTE_THRESHOLD;
//			eindex += EXECUTE_THRESHOLD;
//		}
//		System.out.println("OK");
//	}

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
			// TODO Auto-generated catch block
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
	
//	public void testQuerys(int type) throws InterruptedException {
//		dao = HectorObjectDAO.getInstance();
//		List<LineItem> testobjs = inputRealSPKVObject(from, to, false);
//		for (int i = 0;i < 8;i++)
//		{
//			long t1 = System.currentTimeMillis();
//			if (type == 0)
//			{
//				System.out.println("Point Query");
//				cassandraPointQuery(testobjs);
//			}
//			else if (type == 1)
//			{
//				System.out.println("Range Query");
//				cassandraRangeQuery(testobjs, 300000);
//			}
//			long t2 = System.currentTimeMillis();
//			System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
//			Thread.sleep(500);
//		}
//	}
	
	public int insertObject()
	{
		SPKVObject tObj = testobjs.get(ran.nextInt(testobjs.size()));
		try {
			return clients.get(ran.nextInt(clients.size())).insertObject(tableName, tObj);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	private void batchInsertObject(List<SPKVObject> testobjs, boolean index)
	{
		System.out.print("Insert Data...");
		int ret = 0;
		int sindex = 0; 
		int eindex = EXECUTE_THRESHOLD;
		while (sindex <= testobjs.size())
		{
			eindex = eindex>testobjs.size()?testobjs.size():eindex;
			List<SPKVObject> subList = testobjs.subList(sindex, eindex);
			if (index)
			{
				try {
					Map<String, List<SPKVObject>> objs = new HashMap<String, List<SPKVObject>>();
					objs.put(tableName, subList);
					ret += clients.get(ran.nextInt(clients.size())).batchInsertObjects(objs);		
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("ERROR "+sindex);
					return;
				}
			}
			else
			{
//				ret += dao.insertObject(tableName, subList);
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

	private int getDateDiff(int interval, SimpleDateFormat dfs, long baseTime, String col)
			throws ParseException {
		long lShipdate = (dfs.parse(col).getTime()-baseTime) / interval;
		return Integer.parseInt(lShipdate+"");
	}

	public List<SPKVObject> inputSPKVObject(int start, int end) {
		String fileName = FILE_PATH+tableName+"-"+dimension+"-"+String.format("%02d", offset)+".dat";
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
	
//	public void cassandraRangeQuery(List<LineItem> objs, int dist) {
//		Random ran = new Random();
//		int s = 0;
//		for (int i = 0;i < 10;i++)
//		{
//			LineItem obj = objs.get(ran.nextInt(objs.size()));
//			
//			//范围查询Count
//			int ret = dao.QueryCount(tableName, null, null, false);
//			s += ret;
//		}
//		System.out.println(s);
//	}
//	
//	public void cassandraPointQuery(List<LineItem> objs) {
//		Random ran = new Random();
//		int s = 0;
//		int zero = 0;
//		for (int i = 0;i < 10;i++)
//		{
//			LineItem obj = objs.get(ran.nextInt(objs.size()));
//			//点查询Count
//			int t = dao.QueryCount(tableName, obj.getCols(), obj.getCols(), true);
//			s += t;
//			if (t == 0)
//				zero++;
//		}
//		System.out.println(s);
//		System.out.println(zero);
//	}

	private String getNewLine(String[] cols, String discount) {
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
	
	public int pointQuery()
	{
		SPKVObject obj = testobjs.get(ran.nextInt(testobjs.size()));
		//点查询Count
		try {
			return clients.get(ran.nextInt(10)).pointQuery(tableName, obj.getCols(), contents).size();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public int rangeQuery(int dist) {

		List<SPColumn> ql = new ArrayList<SPColumn>();
		List<SPColumn> qu = new ArrayList<SPColumn>();
		SPKVObject obj = testobjs.get(ran.nextInt(testobjs.size()));
		List<SPColumn> cols = obj.getCols();
//		int a = Integer.parseInt(cols[0]);
//		int b = Integer.parseInt(cols[1]);
//		ql[0] = a-110<100?100:a-110;
//		qu[0] = a+110>500?500:a+110;
//		ql[1] = b-110<100?100:b-110;
//		qu[1] = b+110>500?500:b+110;
		for (int j = 0;j < dimension;j++)
		{
			int col = cols.get(j).getValue();
			int colL = col-dist<lb.get(j)?lb.get(j):col-dist;
			int colU = col+dist>ub.get(j)?ub.get(j):col+dist;
			ql.add(new SPColumn("col"+(j+1), colL));
			ql.add(new SPColumn("col"+(j+1), colU));
		}

//		int quantity = ran.nextInt(10)+15;
//		int dis = ran.nextInt(500);
//		int sd = ran.nextInt(1500);
//		
//		ql.add(0);
//		qu.add(quantity);
//		
//		ql.add(dis);
//		qu.add(dis+100);
//		
//		ql.add(sd);
//		qu.add(sd+365);
//
//		ql.add(sd);
//		qu.add(sd+365);
//
//		ql.add(sd);
//		qu.add(sd+365);
		
		//范围查询
//		System.out.println("L:"+Arrays.toString(ql));
//		System.out.println("U:"+Arrays.toString(qu));
//		List<SPKVObject> ret = iMan.RangeQuery(ql, qu);
//		for (SPKVObject r : ret)
//			System.out.println(Arrays.toString(r.getCols()));
//		System.out.println("--------");
		//范围查询Count
		try {
			return clients.get(ran.nextInt(10)).rangeQueryCount(tableName, ql, qu);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public int knnQuery(int K) {
		SPKVObject obj = testobjs.get(ran.nextInt(testobjs.size()));
//		System.out.println("Query:"+Arrays.toString(v));
		//KNN查询
		List<SPKVObject> res;
		try {
			res = clients.get(ran.nextInt(10)).knnQuery(tableName, obj.getCols(), K, contents);
			return res.size();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
}
