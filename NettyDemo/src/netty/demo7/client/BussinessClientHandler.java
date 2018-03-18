package netty.demo7.client;

import java.util.Date;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.demo7.pojo.PkgEntity;

@Sharable // �Ͽ���handler���Ǳ����ģ�����pipeline���µģ�����Ҫ��sharable���µ�pipeline�ſ�����Ӿɵ�handler
public class BussinessClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("����ʱ���ǣ�"+new Date());
		System.out.println("BussinessClientHandler channelActive");
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ֹͣʱ���ǣ�"+new Date());
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
			System.out.println("�ͻ��˽��յ���������PkgEntity.Pkg����");
		}
		
		ReferenceCountUtil.release(msg);
	}

	private PkgEntity.Pkg createClientHeartBeatRequest() {
		PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
		builder.setReqType(0);
		builder.setData("�յ��������˵���Ӧ");
		return builder.build();
	}

	
}
