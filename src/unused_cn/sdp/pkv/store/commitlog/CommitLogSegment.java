package unused_cn.sdp.pkv.store.commitlog;

import java.util.concurrent.BlockingQueue;

import cn.sdp.pkv.data.type.TableOperate;
import cn.sdp.pkv.store.MapDBConnector;
import cn.sdp.pkv.util.Configs;

public class CommitLogSegment implements Comparable<CommitLogSegment>{ 
	private long ts;
	private int logSize;
	private BlockingQueue<TableOperate> logQueue;
	private final int COMMITLOG_SIZE = Configs.COMMITLOG_SIZE;
	
	public CommitLogSegment(long ts) {
		this.ts = ts;
		logSize = 0;
		logQueue = MapDBConnector.getInstance().getLogdb().getStack("log-"+ts);
	}

	public boolean insertObject(TableOperate iOp) 
	{
		synchronized (this) {
			logSize++;
			if (logSize == COMMITLOG_SIZE)
				return false;
		}
		return logQueue.add(iOp);
	}
	
	public BlockingQueue<TableOperate> getLogOps()
	{
		return logQueue;
	}
	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
	@Override
	public int compareTo(CommitLogSegment o) {
		if (this.ts < o.ts)
			return -1;
		else if (this.ts > o.ts)
			return 1;
		else
			return 0;
	}
}
