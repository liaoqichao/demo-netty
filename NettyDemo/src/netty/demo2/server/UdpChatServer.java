package netty.demo2.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpChatServer {

	private static final int PORT = 12345;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioDatagramChannel.class)
			.localAddress(PORT)
			.option(ChannelOption.SO_BROADCAST, false)
			.handler(new ChannelInitializer<DatagramChannel>(){

				@Override
				protected void initChannel(DatagramChannel ch) throws Exception {
					ch.pipeline().addLast(new UdpChatServerHandler());
				}
				
			});
			
			b.bind(PORT).channel().closeFuture().sync();
			
		} finally {
			group.shutdownGracefully();
		}
	}
}
