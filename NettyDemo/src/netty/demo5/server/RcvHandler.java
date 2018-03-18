package netty.demo5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RcvHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("�ͻ��ˣ�"+ctx.channel().remoteAddress() + "����");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("������������������ȡ������");
		/*
		 * msg�Ǿ�������������õ�����Ϣ������Զ�����ճ������ͽ���Ĺ���
		 */
		ctx.fireChannelRead(msg); // ����Ϣ����һ��������
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("�ͻ��ˣ�"+ctx.channel().remoteAddress() + "����");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("�ͻ��ˣ�"+ctx.channel().remoteAddress() + "�쳣���ѱ��������ر�");
		cause.printStackTrace();
		ctx.close();
	}

	
}
