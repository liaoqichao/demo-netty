package netty.demo6.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import netty.demo6.pojo.PkgEntity;
import netty.demo6.pojo.PkgEntity.Pkg;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

	private int per_5_sec = 0;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
//		System.out.println(3);
	}

//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		System.out.println("svr:3");
//		PkgEntity.Pkg pkg = (PkgEntity.Pkg)(msg);
//		if(pkg.getReqType() == 0){  // 0 正常信息
//			per_5_sec = 0;
//			ctx.fireChannelRead(msg);
//		} else if(pkg.getReqType() == 1){ // 收到客户端的心跳消息
//			System.out.println(ctx.channel().remoteAddress()+"5秒没发送消息");
//			if(per_5_sec >= 2){
//				System.out.println(ctx.channel().remoteAddress()+"10秒没发送消息，关闭连接");
//				ctx.close();
//			} else{
//				ReferenceCountUtil.release(msg);
//			}
//			per_5_sec++;
//		}
//	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			if(event.state() == IdleState.READER_IDLE){ // 5秒没读
				System.out.println("写空闲");
				per_5_sec++;
				if(per_5_sec > 3){
					System.out.println("连续"+per_5_sec+"次写空闲，关闭链接");
					ctx.close();
				}
			} else{
				per_5_sec = 0;
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
	
}
