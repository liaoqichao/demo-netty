package netty.demo6.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.demo6.pojo.PkgEntity;
import netty.demo6.pojo.PkgEntity.Pkg;

public class RcvHandler extends SimpleChannelInboundHandler<PkgEntity.Pkg> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Pkg msg) throws Exception {
		System.out.println("cli:5");
		System.out.println("客户端收到服务器的消息："+msg.getData());
	}

}
