package netty.demo7.client;

import java.util.Date;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.demo7.pojo.PkgEntity;

@Sharable // 断开后handler还是本来的，但是pipeline是新的，必须要有sharable，新的pipeline才可以添加旧的handler
public class BussinessClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("激活时间是："+new Date());
		System.out.println("BussinessClientHandler channelActive");
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("停止时间是："+new Date());
		System.out.println("BussinessClientHandler channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		String message = (String)msg;
//		System.out.println(message);
//		if(message.equals("Heartbeat")){
//			ctx.write("has read message from server");
//			ctx.flush();
//		}
		if(msg instanceof PkgEntity.Pkg){
			PkgEntity.Pkg resp = (PkgEntity.Pkg)msg;
			if(resp.getReqType() == 1){
				ctx.writeAndFlush(createClientHeartBeatRequest());
			}
		} else{
			System.out.println("客户端接收到的请求不是PkgEntity.Pkg类型");
		}
		
		ReferenceCountUtil.release(msg);
	}

	private PkgEntity.Pkg createClientHeartBeatRequest() {
		PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
		builder.setReqType(0);
		builder.setData("收到服务器端的响应");
		return builder.build();
	}

	
}
