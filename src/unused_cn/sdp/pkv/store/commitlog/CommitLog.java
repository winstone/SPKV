package unused_cn.sdp.pkv.store.commitlog;

import cn.sdp.pkv.data.type.TableOperate;

public class CommitLog {
	private static CommitLog instance = new CommitLog();
	private CommitLogManager clMan = CommitLogManager.getInstance();
	private CommitLogExcutorService service = CommitLogExcutorService.getInstance();
	
	private CommitLog() {
		recovery();
	}
	
	public static CommitLog getInstance()
	{
		return instance;
	}
	
	public void writeLog(TableOperate tOp)
	{
		CommitLogSegment clSeg = clMan.getActiveCommitLog(false);
		while (!clSeg.insertObject(tOp))
		{
			clSeg = clMan.getActiveCommitLog(true);
		}
	}
	
	public void setCheckPoint(long ts)
	{
		clMan.setCheckPoint(ts);
	}
	
	public void recovery()
	{
		clMan.recovery();
	}
	
	public void shutdown()
	{
		service.shutdown();
	}
}
