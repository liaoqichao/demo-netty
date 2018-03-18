package netty.demo3.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.demo3.pojo.Message;

public class BussinessHandler extends SimpleChannelInboundHandler<Message> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		System.out.println("clien.BussinessHandler : "+msg);
		System.out.println("cli:5");
	}


}
