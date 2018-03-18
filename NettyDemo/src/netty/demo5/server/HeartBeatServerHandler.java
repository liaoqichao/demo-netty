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
				System.out.println("[5��û���յ��ͻ���"+ctx.channel().id()+"����Ϣ��]");
				if(loss_current_time > 2){ 
					// 10��û�������͹ر��������
					System.out.println("[�رղ���Ծ��channel:"+ctx.channel().id()+"]");
					ctx.close();
				}
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}

	
	
}
