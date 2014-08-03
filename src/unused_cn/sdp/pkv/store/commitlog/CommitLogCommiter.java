package unused_cn.sdp.pkv.store.commitlog;


import org.mapdb.DB;

import cn.sdp.pkv.store.MapDBConnector;

public class CommitLogCommiter implements Runnable {
	private CommitLogArchiver archiver;
	private DB logDb = MapDBConnector.getInstance().getLogdb();
	private CommitLogSegment seg;
	private boolean recover;
	
	public CommitLogCommiter(CommitLogArchiver archiver, CommitLogSegment seg, boolean r) {
		super();
		this.archiver = archiver;
		this.seg = seg;
		this.recover = r;
	}

	@Override
	public void run() {
		try
		{
			logDb.commit();
			long ts = System.currentTimeMillis();
			System.out.println("Log Commit success:"+ts+" segTs:"+seg.getTs());
			archiver.commitCommitLogSegment(seg, ts);
			if (recover)
				archiver.setCheckPoint(ts);
			seg = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
