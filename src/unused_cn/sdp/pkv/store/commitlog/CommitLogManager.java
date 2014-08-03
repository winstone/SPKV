package unused_cn.sdp.pkv.store.commitlog;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.sdp.pkv.data.TableManager;
import cn.sdp.pkv.data.type.TableOperate;
import cn.sdp.pkv.pyramid.PyramidIndexTable;
import cn.sdp.pkv.util.Configs;

public class CommitLogManager {
	private static CommitLogManager instance = new CommitLogManager();
	private CommitLogArchiver archiver;
	private BlockingQueue<CommitLogSegment> curCommitLog;
	private BlockingQueue<CommitLogSegment> availableLogs;
	private CommitLogExcutorService service = CommitLogExcutorService.getInstance();
	private final int AVAILABLE_THRESHOLD = Configs.AVAILABLE_THRESHOLD;
	private final Lock lock = new ReentrantLock();
	
	private CommitLogManager() {
		archiver = CommitLogArchiver.getInstance();
		curCommitLog = new LinkedBlockingQueue<CommitLogSegment>();
		availableLogs = new LinkedBlockingQueue<CommitLogSegment>();
		allocateLogs();
	}
	
	private void allocateLogs()
	{
		if (availableLogs.size() < AVAILABLE_THRESHOLD)
		{
			service.executeTask(new CommitLogAllocator(availableLogs));
		}
	}
	
	public static CommitLogManager getInstance()
	{
		return instance;
	}

	public CommitLogSegment getActiveCommitLog(boolean next) {
		//提交并释放旧的CommitLog对象
		if (next)
		{
			CommitLogSegment toCommit = curCommitLog.poll();
			service.executeTask(new CommitLogCommiter(archiver, toCommit, false));
		}
		
		lock.lock();
		CommitLogSegment ret = curCommitLog.peek();
		if (ret == null)
		{
			ret = availableLogs.poll();
			curCommitLog.add(ret);
			lock.unlock();
			archiver.createCommitLogSegment(ret);
			allocateLogs();
		}
		else
		{
			lock.unlock();
		}
		return ret;
	}
	
	public void recovery()
	{
		TableManager tMan = TableManager.getInstance();
		List<CommitLogSegment> segs = getRecoveryCommitLogs();
		System.out.println("recovery:"+segs.size()+" segs");
		for (CommitLogSegment seg : segs)
		{
			BlockingQueue<TableOperate> logOps = seg.getLogOps();
			
			while (!logOps.isEmpty())
			{
				TableOperate tOp = logOps.poll();
				PyramidIndexTable table = tMan.getTableByName(tOp.getTableName());
				if (tOp.getType() == '1')
					table.syncInsertObject(table.getConverter().OperateToIndex(tOp));
				else
					table.deleteIndexObject(table.getConverter().OperateToIndex(tOp));
			}
			service.executeTask(new CommitLogCommiter(archiver, seg, true));
		}
	}
	
	private List<CommitLogSegment> getRecoveryCommitLogs() {
		List<CommitLogSegment> ret = archiver.getCommitLogSegments();
		return ret;
	}

	public void setCheckPoint(long ts) {
		archiver.setCheckPoint(ts);
	}

}
