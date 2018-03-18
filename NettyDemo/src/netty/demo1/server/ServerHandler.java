package netty.demo1.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("ServerHandler.channelRead");
		/*
		 * 因为发送的是buffer，所以也用buffer接收，直接强转。
		 * NIO要求发送的都是buffer，ByteBuf是netty对NIO的buffer的封装
		 */
		ByteBuf result = (ByteBuf)msg;
		byte[] buff = new byte[result.readableBytes()];
		result.readBytes(buff);
		System.out.println("server accept : "+new String(buff));

		String response = "hello client!";
		byte[] bytes = response.getBytes("UTF-8");
		ByteBuf encoded = ctx.alloc().buffer(bytes.length);
		encoded.writeBytes(response.getBytes());
		
		/*
		 * Unpooled : 辅助类
		 * copiedBuffer：把字节数组包装成buffer对象
		 */
		ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
//		ctx.write(encoded);
//		ctx.flush();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		ctx.flush();
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	
}
