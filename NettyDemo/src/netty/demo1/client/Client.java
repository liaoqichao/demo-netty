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
		// 1. 创建NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// 2. 创建工具类，设置参数
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.SO_SNDBUF, 32*1024)
			.option(ChannelOption.SO_RCVBUF, 32*1024)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 3. 给管道添加handler
					ch.pipeline().addLast(new ClientHandler());
				}
				
			});
			
			// 4. 绑定主机，端口
			ChannelFuture cf = b.connect(host, port).sync();
			
			// 可以直接在这里写数据
//			cf.channel().write("aaa"); // NIO 要求传的是buffr
			
			// 5. channel没关闭，本线程不能关闭
			cf.channel().closeFuture().sync();
		} finally {
			// 6. 释放资源
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Client().run(HOST, 8765);
	}

}
