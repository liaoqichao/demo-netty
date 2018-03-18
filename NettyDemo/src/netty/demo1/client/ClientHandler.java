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
		 * ���ӳɹ�ʱ����
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
		 * ��ȡ���������ص���Ϣ
		 */
		try {
			System.out.println("ClientHandler.channelRead");
			ByteBuf result = (ByteBuf)msg;
			byte[] buff = new byte[result.readableBytes()];
			result.readBytes(buff);
			System.out.println("Server return msg : " + new String(buff));
		} finally {
			/*
			 * ֻ����д���������������buffer��Ҫ�ͷš�
			 * ֻҪ����ctx.write()�����ͻ��Զ��ͷ�msg
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
