package framework_rpc.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import framework_rpc.common.RpcDecoder;
import framework_rpc.common.RpcEncoder;
import framework_rpc.common.RpcRequest;
import framework_rpc.common.RpcResponse;
import framework_rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class RpcServer implements ApplicationContextAware, InitializingBean {

	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	private ServiceRegistry regist;
	private String ServerAddress;

	public RpcServer(String serverAddress, ServiceRegistry regist) {
		ServerAddress = serverAddress;
		this.regist = regist;
	}

	public RpcServer(Map<String, Object> handlerMap, String serverAddress) {
		this.handlerMap = handlerMap;
		ServerAddress = serverAddress;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		Map<String, Object> beanMap = context.getBeansWithAnnotation(RpcService.class);

		if (MapUtils.isNotEmpty(beanMap)) {
			for (Object bean : beanMap.values()) {
				String interfaceName = bean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, bean);
			}

		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChildChannelHandler());
			b.childOption(ChannelOption.SO_KEEPALIVE, true);

			String[] str = ServerAddress.split(":");
			String ip = str[0];
			int port = Integer.parseInt(str[1]);
			// 启动 RPC 服务器
			ChannelFuture future = b.bind(ip, port).sync();
			// 注册 RPC 服务地址
			if (regist != null)
				regist.registry(ServerAddress, handlerMap);
			// 关闭 RPC 服务器
			future.channel().closeFuture().sync();
		} finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			// TODO Auto-generated method stub
			arg0.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
					.addLast(new RpcDecoder(RpcRequest.class)).addLast(new RpcEncoder(RpcResponse.class))
					.addLast(new RpcHandler(handlerMap));
		}

	}

}
