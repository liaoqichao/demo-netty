package netty.demo1.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		/*
		 * 连接成功时调用
		 */
		String msg = "hello server";
		byte[] bytes = msg.getBytes("UTF-8");
		ByteBuf encoded = ctx.alloc().buffer(bytes.length);
		encoded.writeBytes(Unpooled.copiedBuffer(bytes));
		ctx.write(encoded);
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*
		 * 读取服务器返回的信息
		 */
		try {
			System.out.println("ClientHandler.channelRead");
			ByteBuf result = (ByteBuf)msg;
			byte[] buff = new byte[result.readableBytes()];
			result.readBytes(buff);
			System.out.println("Server return msg : " + new String(buff));
		} finally {
			/*
			 * 只读不写的情况，传过来的buffer需要释放。
			 * 只要调用ctx.write()方法就会自动释放msg
			 */
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	
	
}
