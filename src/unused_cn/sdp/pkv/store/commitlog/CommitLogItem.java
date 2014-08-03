package unused_cn.sdp.pkv.store.commitlog;

import java.io.Serializable;

public class CommitLogItem implements Serializable {
	private static final long serialVersionUID = -3993552396018156998L;
	private long segTs;
	private boolean committed;
		
	public CommitLogItem(long segTs, boolean committed) {
		this.segTs = segTs;
		this.committed = committed;
	}
	
	public void setCommitted(boolean committed) {
		this.committed = committed;
	}
	public boolean isCommitted() {
		return committed;
	}
	public void setSegTs(long segTs) {
		this.segTs = segTs;
	}
	public long getSegTs() {
		return segTs;
	} 
}
