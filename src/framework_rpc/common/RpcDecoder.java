package framework_rpc.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 *解码
 * @author haizi
 *
 */
public class RpcDecoder extends ByteToMessageDecoder{
	
	private Class<?> requestClass;
	public RpcDecoder(Class<?> requestClass) {
		this.requestClass = requestClass;
	}

	protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if(buf.readableBytes() < 4){
			return ;
		}
		buf.markReaderIndex();
		int dataLength = buf.readInt();
		if(buf.readableBytes() < dataLength){
			buf.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		buf.readBytes(data);
		Object obj = JsonUtil.deserialize(data, requestClass);
		out.add(obj);
		
	}



}
