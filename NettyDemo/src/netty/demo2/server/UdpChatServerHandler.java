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
		System.out.println("UDP通道已连接");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("UdpChatServerHandler.channelRead0");
		// 1. 获取客户端信息
		System.out.println("消息来源："+msg.sender().getHostString()+":"+msg.sender().getPort());
		System.out.println("本机："+msg.recipient().getHostString()+":"+msg.recipient().getPort());
		
		// 2. 获取数据
		ByteBuf byteBuf = (ByteBuf)msg.copy().content();
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		String content = new String(bytes,CharsetUtil.UTF_8);
		
		// 3. 处理数据
		content = content + "真好";
		System.out.println(content);
		
		// 4.响应客户端
		DatagramPacket dp = new DatagramPacket(Unpooled.copiedBuffer(content,CharsetUtil.UTF_8), msg.sender());
		ctx.writeAndFlush(dp).sync();
	}

}
