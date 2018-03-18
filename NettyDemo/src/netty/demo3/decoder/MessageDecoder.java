package netty.demo3.decoder;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 没有解决粘包问题
		if(in.readableBytes() > 0){
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			String json = new String(bytes,CharsetUtil.UTF_8);
			out.add(json);
			System.out.println("svr:1");
			System.out.println("cli:3");
		} else{
			throw new Exception("收不到数据");
		}
	}

}
