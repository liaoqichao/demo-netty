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
//		if(pkg.getReqType() == 0){  // 0 ������Ϣ
//			per_5_sec = 0;
//			ctx.fireChannelRead(msg);
//		} else if(pkg.getReqType() == 1){ // �յ��ͻ��˵�������Ϣ
//			System.out.println(ctx.channel().remoteAddress()+"5��û������Ϣ");
//			if(per_5_sec >= 2){
//				System.out.println(ctx.channel().remoteAddress()+"10��û������Ϣ���ر�����");
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
			if(event.state() == IdleState.READER_IDLE){ // 5��û��
				System.out.println("д����");
				per_5_sec++;
				if(per_5_sec > 3){
					System.out.println("����"+per_5_sec+"��д���У��ر�����");
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
