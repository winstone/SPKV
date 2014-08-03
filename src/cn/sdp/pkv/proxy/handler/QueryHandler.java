package cn.sdp.pkv.proxy.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import cn.sdp.pkv.data.TableManager;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.thrift.SPContent;
import cn.sdp.pkv.thrift.SPKVObject;
import cn.sdp.pkv.thrift.SPKVRow;

public abstract class QueryHandler extends PKVHandler {
	protected final TableManager tMan = TableManager.getInstance();
	protected final CompletionService<List<SPKVRow>> objServ;
	protected final List<Integer> retCols;

	public QueryHandler(ExecutorService service, List<String> retCols, String tbName) {
		super(tbName);
		this.objServ = new ExecutorCompletionService<List<SPKVRow>>(service);
		this.retCols = getContentIndex(retCols);
	}

	protected List<SPKVObject> getQueryResponse() {
		List<SPKVObject> res = new ArrayList<SPKVObject>();
		try
		{
			for (int i = 0; i < partMan.getEndPoints().size();i++)
			{
				Future<List<SPKVRow>> ret = objServ.take();
				res.addAll(parseFromSPKVRow(ret.get()));
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	protected int getQueryCountResponse() {
		Set<SPKVRow> res = new TreeSet<SPKVRow>();
		try
		{
			for (int i = 0; i < partMan.getEndPoints().size();i++)
			{
				Future<List<SPKVRow>> ret = objServ.take();
				res.addAll(ret.get());
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res.size();
	}

	protected List<Integer> parseFromSPColumn(List<SPColumn> cols)
	{
		List<Integer> ret = new ArrayList<Integer>();
		TableManager tMan = TableManager.getInstance();
		TableInfo info = tMan.getTableByName(tableName).getTbInfo();
		List<String> indexCols = info.getIndexColumn();
		
		if (cols.size() == indexCols.size())
		{
			for (SPColumn col : cols)
				ret.add(col.getValue());
		}
		else
		{
			for (String name : indexCols)
			{
				int i = 0;
				for (i = 0;i < cols.size();++i)
					if (cols.get(i).getName().equals(name))
						break;
				if (i == cols.size())
					ret.add(null);
				else
					ret.add(cols.get(i).getValue());
			}
		}
		return ret;
	}
	
	protected List<SPKVObject> parseFromSPKVRow(List<SPKVRow> rows)
	{
		List<SPKVObject> ret = new ArrayList<SPKVObject>();
		
		TableInfo info = tMan.getTableByName(tableName).getTbInfo();
		List<String> colNames = info.getIndexColumn();
		List<String> contentNames = info.getIndexContent();
		for (SPKVRow row : rows)
		{
			SPKVObject sObj = new SPKVObject();
			sObj.setKey(row.getKey());
			int i = 0;
			for (i = 0;i < colNames.size();++i)
				sObj.addToCols(new SPColumn(colNames.get(i), row.getCols().get(i)));
			i = 0;
			for (int j : retCols)
				sObj.addToContent(new SPContent(contentNames.get(j), row.getContent().get(i++)));
			ret.add(sObj);
		}
		return ret;
	}
	
	protected List<Integer> getContentIndex(List<String> strCols)
	{
		List<Integer> intCols = new ArrayList<Integer>();
		TableInfo info = tMan.getTableByName(tableName).getTbInfo();
		List<String> contentNames = info.getIndexContent();
		for (String name : strCols)
			intCols.add(contentNames.indexOf(name));
		Collections.sort(intCols);
		return intCols;
	}
}
