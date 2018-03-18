package netty.demo7.client;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import netty.demo7.pojo.PkgEntity;

@Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

//	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
//			Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleState state = ((IdleStateEvent) evt).state();
			if(state == IdleState.WRITER_IDLE){
//				1）duplicate方法：复制当前对象，复制后的对象与前对象共享缓冲区，且维护自己的独立索引
//				2）copy方法：复制一份全新的对象，内容和缓冲区都不是共享的
//				3）slice方法：获取调用者的子缓冲区，且与原缓冲区共享缓冲区
//				ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
				
				PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
				builder.setReqType(1); 	// 1表示心跳
				PkgEntity.Pkg req = builder.build();
				ctx.writeAndFlush(req);
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
}
