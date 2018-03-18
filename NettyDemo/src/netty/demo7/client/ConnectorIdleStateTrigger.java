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
//				1��duplicate���������Ƶ�ǰ���󣬸��ƺ�Ķ�����ǰ��������������ά���Լ��Ķ�������
//				2��copy����������һ��ȫ�µĶ������ݺͻ����������ǹ����
//				3��slice��������ȡ�����ߵ��ӻ�����������ԭ��������������
//				ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
				
				PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
				builder.setReqType(1); 	// 1��ʾ����
				PkgEntity.Pkg req = builder.build();
				ctx.writeAndFlush(req);
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
}
