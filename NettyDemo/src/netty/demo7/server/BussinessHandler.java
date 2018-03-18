package netty.demo7.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.demo7.pojo.PkgEntity;

/**
 * ÒµÎñhandler
 */
public class BussinessHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server channelRead...");
		if(msg instanceof PkgEntity.Pkg){
			PkgEntity.Pkg req = (PkgEntity.Pkg)msg;
			if(req.getReqType() == 1){
				System.out.println(ctx.channel().remoteAddress() + " -> Server :heartbeat");
			} else{
				System.out.println(ctx.channel().remoteAddress() + " -> Server :"+req.getData());
			}
			System.out.println();
		} else{
			System.out.println(ctx.channel().remoteAddress() + " -> Server : error request:"+msg.toString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
}
