package cn.sdp.pkv.dht;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.sdp.pkv.data.cassandra.CassandraDAO;
import cn.sdp.pkv.data.cassandra.ICassandra;
import cn.sdp.pkv.util.Configs;

public class PartitionerManager {
	private static PartitionerManager instance = new PartitionerManager();
	private List<EndPoint> endPoints;
	private Murmur3Partitioner partitioner;
	private int replicaFactor = Configs.REPLICA_FACTOR;
	private ICassandra dao = CassandraDAO.getInstance();
	
	private PartitionerManager()
	{
		endPoints = new ArrayList<EndPoint>();
		partitioner = Murmur3Partitioner.getInstance();
		endPoints = dao.getEndPoints();
	}

	public static PartitionerManager getInstance() {
		return instance;
	}
	
	public List<EndPoint> getEndPoints()
	{
		return endPoints;
	}
	
	public ReplicaNodes getDataEndPoints(String key)
	{
		long token = partitioner.getToken(key);
		for (int i = 0;i < endPoints.size();i++)
		{
			if (endPoints.get(i).getToken().compareTo(token) >= 0)
				return _getEndPoints(i);
		}
		return _getEndPoints(0);
	}

	private ReplicaNodes _getEndPoints(int i) {
		ReplicaNodes reNodes = new ReplicaNodes(endPoints.get(i).getIntegerIP());
		for (int j = 1; j < replicaFactor;j++)
			reNodes.addReplica(endPoints.get((i+j)%endPoints.size()).getIntegerIP());
		return reNodes;
	}

	public List<Integer> getMasterIps(String ip) {
		List<Integer> mIps = new ArrayList<Integer>();
		for (int i = 0;i < endPoints.size();i++)
		{
			if (endPoints.get(i).getStringIP().equals(ip))
			{
				for (int j = 1; j < replicaFactor;j++)
					mIps.add(endPoints.get((i-j+endPoints.size())%endPoints.size()).getIntegerIP());
			}
		}
		return mIps;
	}
}
