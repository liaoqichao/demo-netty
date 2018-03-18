package netty.demo2.client;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UdpChatClient {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 12347;
	private static final int SND_PORT = 12345;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioDatagramChannel.class)
			.remoteAddress("127.0.0.1", SND_PORT)
			.option(ChannelOption.SO_BROADCAST, false)
			.handler(new ChannelInitializer<DatagramChannel>(){
				@Override
				protected void initChannel(DatagramChannel ch) throws Exception {
					ch.pipeline().addLast(new UdpChatClientHandler());
				}
			});
			
			Channel channel = b.bind(0).channel();
			channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("ÌìÆø".getBytes(CharsetUtil.UTF_8)),new InetSocketAddress("127.0.0.1",SND_PORT)));
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}
