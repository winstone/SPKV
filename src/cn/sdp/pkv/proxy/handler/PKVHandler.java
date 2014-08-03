package cn.sdp.pkv.proxy.handler;

import java.util.List;

import cn.sdp.pkv.data.TableManager;
import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.dht.PartitionerManager;
import cn.sdp.pkv.proxy.IProxy;
import cn.sdp.pkv.proxy.LocalProxy;

public class PKVHandler {
	protected final LocalProxy local = LocalProxy.getInstance();
	protected final PartitionerManager partMan = PartitionerManager.getInstance();
	protected final String tableName;
	
	public PKVHandler(String tbName)
	{
		tableName = tbName;
	}
}