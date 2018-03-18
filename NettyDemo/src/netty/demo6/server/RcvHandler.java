package netty.demo6.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.demo6.pojo.PkgEntity;

public class RcvHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "������");
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("svr:4");
		PkgEntity.Pkg pkg = (PkgEntity.Pkg)msg;
		System.out.println("�������Ѿ��յ���Ϣ��"+pkg.getData());
		ctx.fireChannelRead(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "�Ͽ����ӣ��������쳣");
		cause.printStackTrace();
		ctx.close();
	}

	
}
