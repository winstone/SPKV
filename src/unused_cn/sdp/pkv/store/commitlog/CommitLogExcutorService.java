package unused_cn.sdp.pkv.store.commitlog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommitLogExcutorService {
	private static CommitLogExcutorService instance = new CommitLogExcutorService();
	private ExecutorService service;
	
	private CommitLogExcutorService()
	{
		service = Executors.newCachedThreadPool();
	}
	
	public static CommitLogExcutorService getInstance()
	{
		return instance;
	}
	
	public void executeTask(Runnable task)
	{
		service.execute(task);
	}

	public void shutdown()
	{
		service.shutdown();
	}
}
