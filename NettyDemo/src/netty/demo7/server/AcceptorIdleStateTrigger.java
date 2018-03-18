package netty.demo7.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 服务器端心跳Handler
 */
@Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleState state = ((IdleStateEvent)evt).state();
			if(state == IdleState.READER_IDLE){
				System.out.println("服务器读空闲，服务器关闭与"+ctx.channel().remoteAddress()+"的链接");
				ctx.close();
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}

	
}
