package cn.sdp.pkv.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
	private static Properties prop;
	
	static {
		prop = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream("spkvconfig.properties");
			prop.load(fis);
			prop.list(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String name)
	{
		return prop.getProperty(name);
	}

	public static int getPropertyInt(String name)
	{
		return Integer.parseInt(prop.getProperty(name));
	}

	public static boolean getPropertyBool(String name) {
		// TODO Auto-generated method stub
		return Boolean.parseBoolean(prop.getProperty(name));
	}
}
