package framework_rpc.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import framework_rpc.registry.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ZkHandler extends SimpleChannelInboundHandler<String> {
	
	private static String address = Constant.ZK_IPADDRESS;
	private static ZooKeeper zk ; 
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private void connect(){
		
		try {
			zk = new ZooKeeper(address,Constant.ZK_SESSION_TIMEOUT,new Watcher(){

				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if(event.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
					
				}
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
		// TODO Auto-generated method stub
		connect();
		List<String> ips = zk.getChildren(Constant.ZK_DATA_PATH, false);
		String ipAddress = null;
		for(String ip : ips){
			byte[] bytes=zk.getData(Constant.ZK_DATA_PATH + "/" + ip, true,null);
			if(arg1.equals(new String(bytes))){
				ipAddress = ip;
				break;
			}
		}
		if(ipAddress == null ){
			ipAddress="####";
		}
		arg0.writeAndFlush(ipAddress);
	}
	
}
