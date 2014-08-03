package unused_cn.sdp.pkv.store.commitlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.BTreeMap;

import cn.sdp.pkv.store.MapDBConnector;

public class CommitLogArchiver {
	private static CommitLogArchiver instance = new CommitLogArchiver();
	private final String CHECK_POINT = "check_point";
	private BTreeMap<String, Long> tbCheckPoint;
	private BTreeMap<Long, CommitLogItem> tbLog;
	private BTreeMap<Long, CommitLogItem> tbLogProgress;

	private CommitLogArchiver()
	{
		tbLog = MapDBConnector.getInstance().getSysDB().getTreeMap("sys_logs");
		tbLogProgress = MapDBConnector.getInstance().getSysDB().getTreeMap("sys_progress_logs");
		tbCheckPoint = MapDBConnector.getInstance().getSysDB().getTreeMap("sys_checkpoints");
		if (tbCheckPoint.get(CHECK_POINT)==null)
			tbCheckPoint.put(CHECK_POINT, System.currentTimeMillis());
	}
	
	public static CommitLogArchiver getInstance()
	{
		return instance;
	}
	
	public void createCommitLogSegment(CommitLogSegment seg)
	{
		CommitLogItem logItem = new CommitLogItem(seg.getTs(), false);
		tbLogProgress.put(seg.getTs(), logItem);
	}
	
	public void commitCommitLogSegment(CommitLogSegment seg, long ts)
	{
		CommitLogItem logItem = tbLogProgress.remove(seg.getTs());
		if (logItem != null)
		{
			logItem = new CommitLogItem(seg.getTs(), true);
			tbLog.put(ts, logItem);
		}
	}
	
	public void setCheckPoint(long ts)
	{
		tbCheckPoint.put(CHECK_POINT, ts);
	}
	
	public List<CommitLogSegment> getCommitLogSegments()
	{
		List<CommitLogSegment> reSegs = new ArrayList<CommitLogSegment>();
		long cpTs = tbCheckPoint.get(CHECK_POINT);
		
		ConcurrentNavigableMap<Long, CommitLogItem> maps = tbLog.tailMap(cpTs);
		for (Entry<Long, CommitLogItem> p : maps.entrySet())
		{
			CommitLogSegment seg = new CommitLogSegment(p.getValue().getSegTs());
			reSegs.add(seg);
		}
		Set<Entry<Long, CommitLogItem>> progress = tbLogProgress.entrySet();
		for (Entry<Long, CommitLogItem> p : progress)
		{
			CommitLogSegment seg = new CommitLogSegment(p.getValue().getSegTs());
			reSegs.add(seg);
		}
		return reSegs;
	}
}
