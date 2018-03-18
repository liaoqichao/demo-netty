package netty.demo6.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import netty.demo6.pojo.PkgEntity;
import netty.demo6.pojo.PkgEntity.Pkg;

public class HeartBeatClientHandler extends ChannelDuplexHandler {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
//		System.out.println(3);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.fireChannelRead(msg);
	}

	private int reader_idle_count = 0;
	private int writer_idle_count = 0;
	private int idle_count = 0;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("cli:4");
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			if(event.state() == IdleState.WRITER_IDLE){ // 5秒没写
				System.out.println("写空闲");
				writer_idle_count++;
				if(writer_idle_count > 1){
					PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
					builder.setReqType(1); // 1表示心跳数据
					builder.setData("heartBeat");
					Pkg pkg = builder.build();
					ctx.channel().writeAndFlush(pkg);
					writer_idle_count = 0;
				}
			} 
//			else if(event.state() == IdleState.READER_IDLE){ // 5秒没读
//				System.out.println("读空闲");
//				reader_idle_count++;
//				if(reader_idle_count > 3){
////					ctx.disconnect(); // disconnect和close的区别？
//				}
//			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
	
	
	
	

}
