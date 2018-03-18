package netty.demo2.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class UdpChatClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("UdpChatClientHandler.channelRead0");
		ByteBuf buf = (ByteBuf)msg.copy().content();
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		String revContent = new String(bytes,CharsetUtil.UTF_8);
		System.out.println(revContent);
	}

}
