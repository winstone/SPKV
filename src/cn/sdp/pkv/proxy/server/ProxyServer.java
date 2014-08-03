package cn.sdp.pkv.proxy.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import cn.sdp.pkv.thrift.PKVLocalService;
import cn.sdp.pkv.util.Configs;

public class ProxyServer extends Thread {
	private final int PROXY_PORT = Configs.PROXY_PORT;
	private TNonblockingServerTransport serverTransport;

	@Override
	public void run() {
		try {
            serverTransport = new TNonblockingServerSocket(PROXY_PORT);
            TTransportFactory transportFactory = new TFramedTransport.Factory();              
            TProtocolFactory proFactory = new TBinaryProtocol.Factory();
            TProcessor processor = new PKVLocalService.Processor(new PKVLocalServiceImpl());
            TServer server = new TThreadedSelectorServer(  
                    new Args(serverTransport)  
                    .protocolFactory(proFactory)  
                    .transportFactory(transportFactory)  
                    .processor(processor)  
                );  

		    System.out.println("Start server on port "+PROXY_PORT+"...");  
		    server.serve(); 
		    
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void shutdown()
	{
	    serverTransport.close();
	}
}
