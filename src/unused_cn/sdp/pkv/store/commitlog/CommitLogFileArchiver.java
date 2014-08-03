package unused_cn.sdp.pkv.store.commitlog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommitLogFileArchiver {
	private static CommitLogFileArchiver instance = new CommitLogFileArchiver();
	private CommitLogExcutorService service = CommitLogExcutorService.getInstance();
	private final Lock fileLock = new ReentrantLock();
	private File logFile;
	
	private CommitLogFileArchiver()
	{
		logFile = new File("D:\\PkvFile\\Data\\log.dat");
		if (!logFile.exists())
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static CommitLogFileArchiver getInstance()
	{
		return instance;
	}
	
	public void insertCommitLogSegment(final CommitLogSegment seg)
	{
		service.executeTask(new Runnable() {
			public void run() {
				BufferedWriter writer;
				try {
					fileLock.lock();
					writer = new BufferedWriter(new FileWriter(logFile, true));
					writer.append(new StringBuilder().append(seg.getTs()).append(",")
							.append('0').append(System.lineSeparator()).toString());
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					fileLock.unlock();
				}
			}
		});
	}
	
	public void removeCommitLogSegment(final CommitLogSegment seg)
	{
		service.executeTask(new Runnable() {
			public void run() {
				RandomAccessFile file;
				try {
					fileLock.lock();
					file = new RandomAccessFile(logFile, "rw");
					long pos = file.getFilePointer();
					while (pos < file.length())
					{
						long ts = Long.parseLong(file.readLine().split(",")[0]);
						if (ts == seg.getTs())
						{
							file.seek(pos);
							file.write(new StringBuilder().append(ts).append(",")
								.append('1').toString().getBytes());
							break;
						}
						pos = file.getFilePointer();
					}		
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				finally {
					fileLock.unlock();
				}
			}
		});
	}
	
	public List<CommitLogSegment> getCommitLogSegments()
	{
		StringBuilder sb = new StringBuilder();
		List<CommitLogSegment> reList = new ArrayList<CommitLogSegment>();
		
		BufferedReader read;
		try {
			read = new BufferedReader(new FileReader(logFile));
			String line = read.readLine();
			while (line != null)
			{
				String[] vals = line.split(",");
				if (vals[1].equals("0"))
				{
					sb.append(line+System.lineSeparator());
					CommitLogSegment seg = new CommitLogSegment(Long.parseLong(vals[0]));
					reList.add(seg);
				}
				line = read.readLine();
			}		
			read.close();
//			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(logFile));
//			out.write(sb.toString().getBytes());
//			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reList;		
	}
}
