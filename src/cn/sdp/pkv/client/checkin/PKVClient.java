package cn.sdp.pkv.client.checkin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

public class PKVClient {
	
	private int dimension = 4;
	private List<Integer> lb = Arrays.asList(-90000, -180000, 0, 0);
	private List<Integer> ub = Arrays.asList(90000, 180000, 800, 90000);
	private List<String> indexCols = Arrays.asList("Latitude", "Longitude", "Date", "Time");
	private List<String> contents = Arrays.asList("Content1", "Content2");
	private final String tableName = "pkv";
	private final int EXECUTE_THRESHOLD = Configs.EXECUTE_THRESHOLD;

//	private List<String> hosts = Arrays.asList("192.168.3.55");
	private List<String> hosts = Arrays.asList("219.224.171.142","219.224.171.141","219.224.171.140","219.224.171.139",
			"219.224.171.138","219.224.171.137","219.224.171.136","219.224.171.135","219.224.171.134","219.224.171.133");
	private final List<PKVService.Client> clients;
	private final List<TTransport> trans;
	private final int from;
	private final int to;
	private final Random ran;
	private List<SPKVObject> testobjs;
	
	private boolean isEnd;

	public PKVClient(int s, int e)
	{
		this.from = s;
		this.to = e;
		this.ran = new Random();
		this.isEnd = false;
		clients = new ArrayList<PKVService.Client>();
		trans = new ArrayList<TTransport>();
		getClientList();
		testobjs = inputSPKVObject();
		System.out.println("Loaded"); 
	}
	
	public boolean run()
	{
		try {

//			long t1 = System.currentTimeMillis();
//			System.out.println("insert:"+from+"-"+to);
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
				System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (TTransport tran : trans)
			tran.close();
		return isEnd;
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
				// TODO Auto-generated catch block
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

	public List<SPKVObject> inputSPKVObject() {
		String fileName = "E:\\checkin_data.txt";
//		String fileName = FILE_PATH+tableName+"-"+dimension+"-"+String.format("%02d", offset)+".dat";
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line = "";
			line.trim();
			for (int i = 0;i < to;i++)
			{
				line = in.readLine();
				if (line == null)
				{
					isEnd = true;
					break;
				}
				if (i >= from)
				{
					String[] cols = line.split("\t");
					if (cols.length > 2)
					{
						String key = cols[1];
						int latitude = ((Double)(Double.parseDouble(cols[2])*1000)).intValue();
						int longitude = ((Double)(Double.parseDouble(cols[3])*1000)).intValue();
						if (latitude > ub.get(0))	latitude = ub.get(0);
						if (longitude > ub.get(1))	longitude = ub.get(1);
						String[] strDate = cols[4].split(" ");
						int date = getDateDiff(strDate[0]);
						int time = getTimeDiff(strDate[1]);
						List<SPContent> content = getContent(cols);
						SPKVObject obj = new SPKVObject();
						obj.setKey(key);
						obj.addToCols(new SPColumn("Latitude", latitude));
						obj.addToCols(new SPColumn("Longitude", longitude));
						obj.addToCols(new SPColumn("Date", date));
						obj.addToCols(new SPColumn("Time",time));
						obj.setContent(content);
						
						objects.add(obj);
					}

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

	private int getTimeDiff(String col) throws ParseException {
		SimpleDateFormat dfs = new SimpleDateFormat("HH:mm:ss");
		Date time = dfs.parse(col);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		return cal.get(Calendar.SECOND);
	}
	
	private int getDateDiff(String col) throws ParseException {
		int interval = 24*60*60*1000;
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		long baseTime = dfs.parse("2010-1-1").getTime();

		long date = (dfs.parse(col).getTime()-baseTime) / interval;
		return ((Long)date).intValue();
	}

	private List<SPContent> getContent(String[] cols) {
		List<SPContent> contents = new ArrayList<SPContent>();
		contents.add(new SPContent("Content1", ""));
		contents.add(new SPContent("Content2", ""));
		return contents;
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
				pyramidRangeQuery(testobjs, 1);
			}
			else if (type == 2)
			{
				System.out.println("KNN Query");
				pyramidKNNQuery(testobjs, 10);
			}
			long t2 = System.currentTimeMillis();
			System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
			Thread.sleep(500);
		}
	}

	
	public void pyramidKNNQuery(List<SPKVObject> objs, int K) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
//			System.out.println("Query:"+Arrays.toString(v));
			//KNN≤È—Ø
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
	
	public void pyramidPointQuery(List<SPKVObject> objs) throws TException {
		Random ran = new Random();
		int s = 0;
		int zero = 0;
		for (int i = 0;i < 1000;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			//µ„≤È—ØCount
			int t = clients.get(ran.nextInt(clients.size())).pointQuery(tableName, obj.getCols(), contents).size();
			s += t;
			if (t == 0)
				zero++;
		}
		System.out.println(s);
		System.out.println(zero);
	}
	
	public void pyramidRangeQuery(List<SPKVObject> objs, double percent) throws TException {
		Random ran = new Random();
		int s = 0;
		percent /= 100;
		for (int i = 0;i < 10;i++)
		{
			SPKVObject obj = objs.get(ran.nextInt(objs.size()));
			
			List<SPColumn> ql = new ArrayList<SPColumn>();
			List<SPColumn> qu = new ArrayList<SPColumn>();
			List<SPColumn> cols = obj.getCols();

			int col = cols.get(0).getValue();
			int dist = (int) (percent*10000);
			int colL = col-dist<lb.get(0)?lb.get(0):col-dist;
			int colU = col+dist>ub.get(0)?ub.get(0):col+dist;
			ql.add(new SPColumn("Latitude", colL));
			qu.add(new SPColumn("Latitude", colU));
			col = cols.get(1).getValue();
			dist = (int) (percent*10000);
			colL = col-dist<lb.get(1)?lb.get(1):col-dist;
			colU = col+dist>ub.get(1)?ub.get(1):col+dist;
			ql.add(new SPColumn("Longitude", colL));
			qu.add(new SPColumn("Longitude", colU));
			col = cols.get(2).getValue();
			dist = (int) (percent*100);
			colL = col-dist<lb.get(2)?lb.get(2):col-dist;
			colU = col+dist>ub.get(2)?ub.get(2):col+dist;
			ql.add(new SPColumn("Date", colL));
			qu.add(new SPColumn("Date", colU));
			col = cols.get(3).getValue();
			dist = (int) (percent*10000);
			colL = col-dist<lb.get(3)?lb.get(3):col-dist;
			colU = col+dist>ub.get(3)?ub.get(3):col+dist;
			ql.add(new SPColumn("Time", colL));
			qu.add(new SPColumn("Time", colU));
			//∑∂Œß≤È—Ø
//			System.out.println("L:"+Arrays.toString(ql));
//			System.out.println("U:"+Arrays.toString(qu));
//			List<SPKVObject> ret = iMan.RangeQuery(ql, qu);
//			for (SPKVObject r : ret)
//				System.out.println(Arrays.toString(r.getCols()));
//			System.out.println("--------");
			//∑∂Œß≤È—ØCount
			int ret = clients.get(ran.nextInt(clients.size())).rangeQueryCount(tableName, ql, qu);
			s += ret;
		}
		System.out.println(s);
	}
}
