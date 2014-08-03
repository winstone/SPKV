package cn.sdp.pkv.test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cn.sdp.pkv.data.type.OperateType;
import cn.sdp.pkv.data.type.TableOperate;
//import cn.sdp.pkv.store.commitlog.CommitLog;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.store.model.ValueObject;

public class CommitLogTest {
//	private static CommitLog cmLog = CommitLog.getInstance();
//	
//	public static void main(String[] args) throws Exception {
//		Runnable run = new Runnable() {
//			public void run() {
//				long t1 = System.currentTimeMillis();
//				Integer[] arr = {5, 10, 15};
//				for (int i = 0;i < 110000;i++)
//				{
//					String content = getMD5(i+"");
//					List<String> contents = new ArrayList<String>();
//					contents.add(content);
//					cmLog.writeLog(new TableOperate('1', "", content, new ValueObject(Arrays.asList(arr), contents, 0)));
//				}
//				long t2 = System.currentTimeMillis();
//				System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
//			}
//		};
//		
//		new Thread(run).start();
//		new Thread(run).start();
//		new Thread(run).start();
//		new Thread(run).start();
//		Scanner scan = new Scanner(System.in);
//		scan.nextLine();
//		cmLog.shutdown();
//		
////		long t1 = System.currentTimeMillis();
////		cmLog.recovery();
////		long t2 = System.currentTimeMillis();
////		System.out.println(String.format("Time:%.3f",(t2-t1)/1000.0));
////		cmLog.shutdown();
//	}
//
//	private static String getMD5(String str)
//	{
//		byte[] md5byte = null;
//		try {
//			MessageDigest md5 = MessageDigest.getInstance("md5");
//			md5.update(str.getBytes("utf-8"));
//			md5byte = md5.digest();
//		
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return byteToString(md5byte);
//	}
//
//	private static String byteToString(byte[] buf)
//	{
//		String result = "";
//
//		for (int i = 0;i < buf.length; i++)
//		{
//			result += Integer.toString((buf[i]&0xff)+0x100,16).substring(1).toUpperCase();
//		}
//		return result;
//	}			
}
