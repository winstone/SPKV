package cn.sdp.pkv.store;

import java.io.File;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.util.Configs;


public class MapDBConnector {
	private static MapDBConnector instance = new MapDBConnector();
	private DB db;
	private DB logdb;
	private DB sysdb;
	private String path;
	private File dbFile;
	private File logdbFile;
	private File sysdbFile;

	private MapDBConnector()
	{
		path = Configs.FILE_PATH+"Data"+File.separator+"log"+File.separator;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		path = path.substring(0, path.length()-4);
		logdbFile = new File(path+"log"+File.separator+"logdb");
		sysdbFile = new File(path+"sysdb");
		dbFile = new File(path+"indexdb");
		sysdb = DBMaker.newFileDB(sysdbFile).cacheDisable().asyncWriteDisable()
					.closeOnJvmShutdown().transactionDisable().make();
		db = DBMaker.newFileDB(dbFile).cacheDisable().asyncWriteDisable()
					.closeOnJvmShutdown().transactionDisable().make();
		logdb = DBMaker.newAppendFileDB(logdbFile).cacheDisable().asyncWriteDisable().closeOnJvmShutdown().transactionDisable().make();
	}

	public static MapDBConnector getInstance() {
		return instance;
	}
	
	public DB getDB()
	{
		return db;
	}
	public DB getLogdb() {
		return logdb;
	}

	public DB getSysDB() {
		return sysdb;
	}
}
