package netty.demo5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RcvHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端："+ctx.channel().remoteAddress() + "上线");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("服务器结束处理器读取完数据");
		/*
		 * msg是经过解码器解码得到的消息，框架自动做好粘包拆包和解码的工作
		 */
		ctx.fireChannelRead(msg); // 把消息给下一个处理器
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端："+ctx.channel().remoteAddress() + "掉线");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("客户端："+ctx.channel().remoteAddress() + "异常，已被服务器关闭");
		cause.printStackTrace();
		ctx.close();
	}

	
}
