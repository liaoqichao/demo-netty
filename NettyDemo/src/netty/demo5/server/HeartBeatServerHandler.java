package netty.demo5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

	private int loss_current_time = 0;
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) { 
			IdleStateEvent event = (IdleStateEvent)evt;
			if(event.state() == IdleState.READER_IDLE){
				loss_current_time++;
				System.out.println("[5秒没接收到客户端"+ctx.channel().id()+"的消息了]");
				if(loss_current_time > 2){ 
					// 10秒没有心跳就关闭这个链接
					System.out.println("[关闭不活跃的channel:"+ctx.channel().id()+"]");
					ctx.close();
				}
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}

	
	
}
