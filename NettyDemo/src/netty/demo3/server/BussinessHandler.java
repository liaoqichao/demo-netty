package netty.demo3.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.demo3.pojo.Message;

public class BussinessHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("svr:3");
		Message message = (Message)msg;
		message.setRcv("�������յ�");
		System.out.println(message);
		ctx.channel().writeAndFlush(msg);
	}

}
