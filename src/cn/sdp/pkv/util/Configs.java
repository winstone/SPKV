package cn.sdp.pkv.util;


public class Configs {
	public static final String FILE_PATH = ConfigReader.getProperty("FILE_PATH");
	public static final int EXECUTE_THRESHOLD = ConfigReader.getPropertyInt("EXECUTE_THRESHOLD");
	public static final int DIMENSION_THRESHOLD = ConfigReader.getPropertyInt("DIMENSION_THRESHOLD");
	public static final int INDEX_PORT = ConfigReader.getPropertyInt("INDEX_PORT");
	public static final int PROXY_PORT = ConfigReader.getPropertyInt("PROXY_PORT");
	public static final int AVAILABLE_THRESHOLD = ConfigReader.getPropertyInt("AVAILABLE_THRESHOLD");
	public static final int COMMITLOG_SIZE = ConfigReader.getPropertyInt("COMMITLOG_SIZE");
	public static final int MEMTABLE_THRESHOLD = ConfigReader.getPropertyInt("MEMTABLE_THRESHOLD");
	public static final boolean IS_EXTEND = ConfigReader.getPropertyBool("IS_EXTEND");
	public static final int REPLICA_FACTOR = ConfigReader.getPropertyInt("REPLICA_FACTOR");
	public static final String LOCAL_IP = ConfigReader.getProperty("LOCAL_IP");
	public static final String DB_IP = ConfigReader.getProperty("DB_IP");
	public static final int DB_PORT = ConfigReader.getPropertyInt("DB_PORT");
	public static final int QUERY_DB = ConfigReader.getPropertyInt("QUERY_DB");
	public static final boolean MINIMUM = ConfigReader.getPropertyBool("MINIMUM");
	public static final int OFFSET = ConfigReader.getPropertyInt("OFFSET");
	public static final int PEER_TIMEOUT = ConfigReader.getPropertyInt("PEER_TIMEOUT");
	
	public static int getLocalIntIP() {
		return PKVConverter.getIntegerIP(LOCAL_IP);
	}
}
