package netty.demo5.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.demo5.pojo.MessageEntity;

public class RcvHandler extends SimpleChannelInboundHandler<MessageEntity.Message> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageEntity.Message msg) throws Exception {
		System.out.println(msg.getRcv());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	

}
