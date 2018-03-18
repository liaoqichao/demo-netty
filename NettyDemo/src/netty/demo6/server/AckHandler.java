package netty.demo6.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.demo6.pojo.PkgEntity;

public class AckHandler extends ChannelInboundHandlerAdapter {


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
//		while(true){
//			
//			PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
//			builder.setReqType(0);
//			builder.setData(" ok!");
//
//			PkgEntity.Pkg resp = builder.build();
//			
//			ctx.writeAndFlush(resp);
//			Thread.sleep(1000);
//		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			System.out.println("svr:5");
			PkgEntity.Pkg req = (PkgEntity.Pkg)(msg);
			
			PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
			builder.setReqType(0);
			builder.setData(req.getData()+" ok!");

			PkgEntity.Pkg resp = builder.build();
			
			ctx.writeAndFlush(resp);
		} finally {
			ReferenceCountUtil.release(msg);
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+"断开连接，服务器异常");
		cause.printStackTrace();
		ctx.close();
	}
	
}
