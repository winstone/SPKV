package cn.sdp.pkv.dht;

import java.util.ArrayList;
import java.util.List;

public class ReplicaNodes {
	private int master;
	private List<Integer> replicas;
	
	public ReplicaNodes(int master)
	{
		this.setMaster(master);
		replicas = new ArrayList<Integer>();
	}
	
	public void addReplica(int replica) {
		this.replicas.add(replica);
	}
	public List<Integer> getReplicas() {
		return replicas;
	}
	public void setMaster(int master) {
		this.master = master;
	}
	public int getMaster() {
		return master;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + master;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReplicaNodes other = (ReplicaNodes) obj;
		if (master != other.master)
			return false;
		return true;
	}
	
}
