package cn.sdp.pkv.proxy.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import cn.sdp.pkv.data.TableManager;
import cn.sdp.pkv.pyramid.TableInfo;
import cn.sdp.pkv.thrift.IndexInfo;
import cn.sdp.pkv.thrift.PKVLocalService;
import cn.sdp.pkv.thrift.PKVService;
import cn.sdp.pkv.thrift.SPColumn;
import cn.sdp.pkv.util.Configs;
import cn.sdp.pkv.util.PKVConverter;

public abstract class PeerRequest {
	protected int mIp;
	protected String destIP;
	protected String repIP;
	protected PKVLocalService.Client client;

	public PeerRequest(String dest, int mIp, String repIp) {
		this.mIp = mIp;
		this.destIP = dest;
		this.repIP = repIp;
	}
	
	protected TTransport openConnect() throws TTransportException {
		TSocket socket = new TSocket(this.destIP, Configs.PROXY_PORT);
		socket.setTimeout(Configs.PEER_TIMEOUT);
		TTransport transport = new TFramedTransport(socket);     
		TProtocol protocol = new TBinaryProtocol(transport);  
		client = new PKVLocalService.Client(protocol);  
		transport.open();
//		System.out.println("Connected to "+destIP+" rf:"+PKVConverter.getStringIP(mIp));
		return transport;
	}  

}
