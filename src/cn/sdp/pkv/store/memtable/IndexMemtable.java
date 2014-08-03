package cn.sdp.pkv.store.memtable;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import cn.sdp.pkv.pyramid.index.Pyramid;
import cn.sdp.pkv.store.dao.IndexDAO;
import cn.sdp.pkv.store.model.IndexObject;
import cn.sdp.pkv.store.model.ValueObject;
import cn.sdp.pkv.util.Configs;

public class IndexMemtable extends IndexDAO {
	private final ConcurrentSkipListMap<String, ValueObject> memtable;
	private final AtomicInteger size;
	private final int MEMTABLE_THRESHOLD = Configs.MEMTABLE_THRESHOLD;
	
	public IndexMemtable(Pyramid py)
	{
		memtable = new ConcurrentSkipListMap<String, ValueObject>();
		size = new AtomicInteger(0);
		this.py = py;
	}

	@Override
	protected ConcurrentNavigableMap<String, ValueObject> rangeResult(
			String key1, String key2) {
		String skey1 = key1;
		String skey2 = new BigDecimal(key2).add(new BigDecimal("0.00000001")).toString();

		return memtable.subMap(skey1, skey2);
	}


	@Override
	protected int operateInsert(List<IndexObject> objs) {
		int ret = 0;
		for (IndexObject iObj : objs)
		{
			ret = operateInsert(iObj);
			if (ret != 0)
				return ret;
		}
		return ret;
	}
	
	@Override
	protected int operateInsert(IndexObject iObj) {
		// TODO Auto-generated method stub
		if (size.get() >= MEMTABLE_THRESHOLD)
		{
			releaseMemtable();
			size.set(0);
		}
		ValueObject old = memtable.put(iObj.getKey(), iObj.getObj());
		if (old != null && iObj.getObj().getTimestamp() < old.getTimestamp())
		{
			memtable.put(iObj.getKey(), old);
		}
		else
		{
			size.getAndIncrement();
		}
		return 0;
	}
	
	public void releaseMemtable()
	{
		memtable.clear();
	}

	@Override
	protected int operateDelete(IndexObject obj) {
		memtable.remove(obj.getKey());
		return 0;
	}
}
