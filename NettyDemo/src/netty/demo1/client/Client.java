package netty.demo1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	private final static String HOST = "127.0.0.1";
	
	public void run(final String host, final int port)throws Exception{
		// 1. ����NIO�߳���
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// 2. ���������࣬���ò���
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.SO_SNDBUF, 32*1024)
			.option(ChannelOption.SO_RCVBUF, 32*1024)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 3. ���ܵ����handler
					ch.pipeline().addLast(new ClientHandler());
				}
				
			});
			
			// 4. ���������˿�
			ChannelFuture cf = b.connect(host, port).sync();
			
			// ����ֱ��������д����
//			cf.channel().write("aaa"); // NIO Ҫ�󴫵���buffr
			
			// 5. channelû�رգ����̲߳��ܹر�
			cf.channel().closeFuture().sync();
		} finally {
			// 6. �ͷ���Դ
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Client().run(HOST, 8765);
	}

}
