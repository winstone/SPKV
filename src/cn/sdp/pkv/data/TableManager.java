package cn.sdp.pkv.data;

import java.util.HashMap;
import java.util.Map;

import org.mapdb.BTreeMap;

import cn.sdp.pkv.pyramid.PyramidIndexTable;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.store.MapDBConnector;
import cn.sdp.pkv.thrift.IndexInfo;

public class TableManager {
	private static TableManager instance = new TableManager();
	private Map<String, PyramidIndexTable> tbMap;
	private BTreeMap<String, TableInfo> tbTree;
	
	private TableManager()
	{
		tbMap = new HashMap<String, PyramidIndexTable>();
		tbTree = MapDBConnector.getInstance().getSysDB().getTreeMap("sys_tables");
		init();
	}
	
	public static TableManager getInstance()
	{
		return instance;
	}
	
	private void init()
	{
		for (String tbName : tbTree.keySet())
		{
			TableInfo tbInfo = tbTree.get(tbName);
			if (tbInfo != null)
			{
				System.out.println("Load Table:"+tbInfo.getTableName()+" dimension:"+tbInfo.getDimension());
				PyramidIndexTable table = new PyramidIndexTable(tbInfo);
				tbMap.put(tbInfo.getTableName(), table);
//				System.out.println(tbInfo.getLb().get(5));
//				System.out.println(tbInfo.getUb().get(5));
//				System.out.println(tbInfo.getIndexColumn().get(5));
			}
		}
	}
	
	public PyramidIndexTable getTableByName(String tbName)
	{
		PyramidIndexTable tb = tbMap.get(tbName);
		return tb;
	}

	public int createNewTable(TableInfo tbInfo)
	{
		if (tbMap.get(tbInfo.getTableName()) == null)
		{
			PyramidIndexTable table = new PyramidIndexTable(tbInfo);
			tbMap.put(tbInfo.getTableName(), table);
			tbTree.put(tbInfo.getTableName(), tbInfo);
			return 0;
		}
		else 
			return -4;
	}
}
