package unused_cn.sdp.pkv.store.commitlog;

import java.util.concurrent.BlockingQueue;

public class CommitLogAllocator implements Runnable {
	private BlockingQueue<CommitLogSegment> allocateLogs;

	public CommitLogAllocator(BlockingQueue<CommitLogSegment> allocateLogs) {
		super();
		this.allocateLogs = allocateLogs;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			for (int i = 0;i < 3;i++)
			{
				long ts = System.currentTimeMillis();
				System.out.println("allocateLogs"+ts);
				CommitLogSegment segment = new CommitLogSegment(ts);
				
				this.allocateLogs.add(segment);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
