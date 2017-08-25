package framework_rpc.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("rawtypes")
public class RpcEncoder extends MessageToByteEncoder{
	
	private Class<?> responseClass ;

	
	
	public RpcEncoder(Class<?> responseClass) {
		super();
		this.responseClass = responseClass;
	}



	@Override
	protected void encode(ChannelHandlerContext context, Object obj, ByteBuf buf) throws Exception {
		// TODO Auto-generated method stub
		if(responseClass.isInstance(obj)){
			byte[] data = JsonUtil.serialize(buf);
			buf.writeInt(data.length);
            buf.writeBytes(data);
		}
	}


}
