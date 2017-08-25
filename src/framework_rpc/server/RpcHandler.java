package framework_rpc.server;

import java.lang.reflect.Method;
import java.util.Map;


import framework_rpc.common.RpcRequest;
import framework_rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest>{
	private Map<String, Object> handlerMap;
	
	public RpcHandler(Map<String, Object> handlerMap) {
		super();
		this.handlerMap = handlerMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, RpcRequest req) throws Exception {
		// TODO Auto-generated method stub
		
		Object obj = handlerMap.get(req.getClassName());
		if(obj != null){
			Method method = req.getClass().getDeclaredMethod(req.getMethodName(), req.getParamTypes());
			Object result = method.invoke(obj, req.getParams());
			RpcResponse response = new RpcResponse();
			response.setRequestId(req.getRequestId());
			response.setResult(result);
			context.writeAndFlush(response);
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
	
	

}
