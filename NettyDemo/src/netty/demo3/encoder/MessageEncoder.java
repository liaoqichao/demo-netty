package netty.demo3.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class MessageEncoder extends MessageToByteEncoder<String> {

	
	
	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		try{
//			System.out.println("MessageEncoder : "+msg.getBytes(CharsetUtil.UTF_8));
			out.writeBytes(msg.getBytes(CharsetUtil.UTF_8));
			System.out.println("cli:2");
			System.out.println("svr:5");
			ctx.channel().writeAndFlush(out);
		} finally {
			ReferenceCountUtil.release(out);  // 写完要释放资源
		}
	}

}
