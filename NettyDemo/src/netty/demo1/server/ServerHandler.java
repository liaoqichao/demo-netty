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
		 * ��Ϊ���͵���buffer������Ҳ��buffer���գ�ֱ��ǿת��
		 * NIOҪ���͵Ķ���buffer��ByteBuf��netty��NIO��buffer�ķ�װ
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
		 * Unpooled : ������
		 * copiedBuffer�����ֽ������װ��buffer����
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
