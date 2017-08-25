package framework_rpc.client;

import framework_rpc.common.RpcDecoder;
import framework_rpc.common.RpcEncoder;
import framework_rpc.common.RpcRequest;
import framework_rpc.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{

	private String host;
	private int port;
	private RpcResponse response; 
	
	private EventLoopGroup group = new NioEventLoopGroup();
	public RpcClient(RpcRequest request ,String ipAddress) {
		this.host = ipAddress.split(":")[0];
		this.port = Integer.parseInt(ipAddress.split(":")[1]);
		
	}

	public RpcResponse sendRequest(RpcRequest request) {
		try {
			Bootstrap boot = getBootstrap();
			ChannelFuture f = boot.connect(host, port).sync();
			Channel channel = f.channel();
			//发送请求
			channel.writeAndFlush(request).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
		// 返回 RPC 响应对象
		return response;
	}
	public void close(){
		group.shutdownGracefully();
	}
	public Bootstrap getBootstrap(){
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// TODO Auto-generated method stub
				ch.pipeline().addLast(new RpcEncoder(RpcRequest.class)).addLast(new RpcDecoder(RpcResponse.class)).addLast(RpcClient.this);
			}
		});
		// 不延迟发送数据
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		return bootstrap;
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, RpcResponse response) throws Exception {
		// TODO Auto-generated method stub
		this.response = response;
		
	}


}
