package cn.sdp.pkv.test;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.xerial.snappy.Snappy;

import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.data.cassandra.ICassandra;
import cn.sdp.pkv.dht.PartitionerManager;
import cn.sdp.pkv.dht.ReplicaNodes;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public class cassandraTest {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
//		CassandraDAO dao = CassandraDAO.getInstance();
		inputObject();
		
//		dao.getRing("testdb");
//		dao.getTokenMap();
//		dao.getEndPoint();
//		HectorObjectDAO hDao = HectorObjectDAO.getInstance();
//		hDao.getTokenMap();
//		PartitionerManager part = PartitionerManager.getInstance();
//		ReplicaNodes node = part.getDataEndPoints("1035titionerMan5");
//		System.out.println(PKVConverter.getIntegerIP("192.168.3.243"));
//		System.out.println(PKVConverter.getIntegerIP("192.168.3.98"));
//		System.out.println(PKVConverter.getStringIP(node.getReplicas().get(0))+"-"+PKVConverter.getStringIP(node.getReplicas().get(1)));
//		String testString = "levelDB��log�ļ���LevelDb�е���Ҫ������ϵͳ����;�ָ�ʱ���ܹ���֤���ᶪʧ���ݡ���Ϊ�ڽ���¼д���ڴ��Memtable֮ǰ������д��Log�ļ���������ʹϵͳ�������ϣ�Memtable�е�����û�����ü�Dump�����̵�SSTable�ļ���LevelDBҲ���Ը���log�ļ��ָ��ڴ��Memtable���ݽṹ���ݣ��������ϵͳ��ʧ���ݣ��������LevelDb��Bigtable��һ�µġ� ";
//		System.out.println(testString.substring(testString.indexOf(";")+1));
//		System.out.println(Configs.LOCAL_IP);
	}
	
	private static void inputObject()
	{

		String fileName = "E:\\checkin_data.txt";
		List<SPKVObject> objects = new ArrayList<SPKVObject>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
			String line = "";
			int i = 0;
			while (line != null)
			{
				line = in.readLine();
//				if (i >= from)
//				{
//					String[] cols = line.split("\t");
//					int[] nums = new int[3];
//					nums[0] = Integer.parseInt(cols[2]);
//					if (nums[0] > 30000)
//						nums[0] = 30000;
//					nums[1] = Integer.parseInt(cols[3]);
//					if (nums[1] > 30000)
//						nums[1] = 30000;
//					nums[2] = Integer.parseInt(cols[4]);
//					if (nums[2] > 5000)
//						nums[2] = 5000;
//					SPKVObject obj = new SPKVObject();
//					obj.setKey(cols[0]);
//					obj.addToCols(new SPColumn("col1", Integer.parseInt(cols[1])));
//					for (int j = 2;j <= 4;++j)
//						obj.addToCols(new SPColumn("col"+j, nums[j-2]));
//					obj.addToContent(new SPContent("content", cols[0]));
//					
//					objects.add(obj);
//				}
				i++;
				if (i % 1000000 == 0)
					System.out.println(i);
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
		}
	}

	private static String getMD5(String str)
	{
		byte[] md5byte = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(str.getBytes("utf-8"));
			md5byte = md5.digest();
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
