package framework_rpc.zookeeper;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ConnectManage {
	
	private EventLoopGroup bossGroup = new  NioEventLoopGroup();
	private EventLoopGroup workGroup = new NioEventLoopGroup();
	private ServerBootstrap boot = new ServerBootstrap();
	private  ConnectManage() {
		// TODO Auto-generated constructor stub
	}
	
	private static class Inner{
		private static ConnectManage connect = new ConnectManage(); 
	}
	
	public String discoveryServiceAddress(String interfaceName){
		boot.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new Handler());
		
		return null;
	}
	public static ConnectManage getInstance(){
		return Inner.connect;
	}
	
	private class Handler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			// TODO Auto-generated method stub
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024)).addLast(new StringDecoder()).addLast(new StringEncoder()).addLast(new ZkHandler());
		}
		
	} 

}
