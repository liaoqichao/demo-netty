package netty.demo2.server;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class UdpChatServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("UDPͨ��������");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("UdpChatServerHandler.channelRead0");
		// 1. ��ȡ�ͻ�����Ϣ
		System.out.println("��Ϣ��Դ��"+msg.sender().getHostString()+":"+msg.sender().getPort());
		System.out.println("������"+msg.recipient().getHostString()+":"+msg.recipient().getPort());
		
		// 2. ��ȡ����
		ByteBuf byteBuf = (ByteBuf)msg.copy().content();
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		String content = new String(bytes,CharsetUtil.UTF_8);
		
		// 3. ��������
		content = content + "���";
		System.out.println(content);
		
		// 4.��Ӧ�ͻ���
		DatagramPacket dp = new DatagramPacket(Unpooled.copiedBuffer(content,CharsetUtil.UTF_8), msg.sender());
		ctx.writeAndFlush(dp).sync();
	}

}
