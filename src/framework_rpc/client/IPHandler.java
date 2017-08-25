package framework_rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class IPHandler extends SimpleChannelInboundHandler<String> {

	private final ByteBuf buf;
	private String host;
	private int port;
	private String ipAddress;
	EventLoopGroup group = new NioEventLoopGroup();

	public IPHandler(String message, String host, int port) {
		byte[] bytes = message.getBytes();
		buf = Unpooled.buffer(bytes.length);
		buf.writeBytes(bytes);
		this.host = host;
		this.port = port;
	}

	public String getIpAddress() {
		Bootstrap bootstrap = getBootstrap();
		// 连接 RPC 服务器
		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			// 写入 RPC 请求数据并关闭连接
			Channel channel = future.channel();
			// channel.writeAndFlush(request).sync();
			channel.closeFuture().sync();
			// 返回 RPC 响应对象
			return ipAddress;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		return ipAddress;
	}

	private Bootstrap getBootstrap() {
		// TODO Auto-generated method stub
		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(group).channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel arg0) throws Exception {
				// TODO Auto-generated method stub
				ChannelPipeline pipeline = arg0.pipeline();
				pipeline.addLast(new StringEncoder());
				pipeline.addLast(new StringDecoder());
				pipeline.addLast(IPHandler.this);
			}
		});
		// 不延迟发送数据
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		return bootstrap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, String ipAddress) throws Exception {
		// TODO Auto-generated method stub
		this.ipAddress = ipAddress;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		ctx.writeAndFlush(buf);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}

}
