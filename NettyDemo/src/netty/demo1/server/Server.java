package netty.demo1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

	/**
	 * ChannelOption.SO_BACKLOG参数：TCP缓冲区，表示TCP内核为相应套接字排队的最大连接个数
	 * 第一次握手：
	 * TCP在建立连接时，客户端会向服务器端发送SYN(SYN=j)
	 * 第二次握手：
	 * 服务器端接收到客户端的SYN之后，确认客户端的SYN（ack=j+1），同时向客户端发送一个SYN包（syn=k）
	 * 即SYN+ACK包。然后发送SYN ACK给客户端。
	 * 此时，TCP会把客户端连接放到A队列中，然后给发送SYN ACK给客户端。
	 * 发送给客户端后，会把刚才在A队列的连接放到B队列中。
	 * 第三次握手：
	 * 客户端收到SYN后，给服务器端发送ACK。
	 * 服务器端接收到ACK后会把B队列的连接取出，从而完成三次握手。应用程序的accept返回。
	 * 
	 * A队列和B队列的长度之和是BACKLOG。当A、B队列长度大于BACKLOG，最后面新的连接都会被TCP内核拒绝。
	 * 所以BACKLOG过小，accept()速度会跟不上，A、B队列满了导致新的客户端无法连接。
	 * 
	 * BACKLOG对程序支持的连接数并无影响，BACKLOG影响的只是还没被accept取出的连接
	 * 
	 * 实际中BACKLOG的值在100多
	 * 
	 * Channel是线程安全的
	 * 
	 */
	
	private final int port;
	
	public Server(int port){
		this.port = port;
	}
	
	public static void main(String[] args) {
		
		new Server(8765).start();
		
	}

	private void start() {
		/*
		 * 1. 建立2个线程组，分别用于处理客户端通道的accept事件和处理读写
		 * 2. 绑定服务端通道NioServerSocketChannel
		 * 3. 给读写事件线程通道绑定ServerHandler去处理真正的读写
		 * 4. 监听端口
		 */
		
		// 1. 创建2个线程组
		EventLoopGroup parentGroup = new NioEventLoopGroup();		// 用于处理服务器端接收客户端连接，即处客户端通道的accept事件
		EventLoopGroup childGroup = new NioEventLoopGroup();		// 用于网络通信（网络读写），处理读写事件
		try {
			// 2.创建工具类，绑定线程组，绑定端口，设置参数，绑定childHandler
			ServerBootstrap b = new ServerBootstrap();				// 创建辅助工具类，用于服务器通道的一些配置
			b.group(parentGroup, childGroup)						// 绑定2个线程
			.channel(NioServerSocketChannel.class)					// 指定NIO模式
			.option(ChannelOption.SO_BACKLOG, 1024)					// 设置TCP缓冲区
			.childOption(ChannelOption.SO_SNDBUF, 32*1024)				// 设置发送缓冲大小
			.childOption(ChannelOption.SO_RCVBUF, 32*1024)				// 设置接收缓冲大小
			.childOption(ChannelOption.SO_KEEPALIVE, true)				// 保持连接，默认是true
			.childHandler(new ChannelInitializer<SocketChannel>(){
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					/*
					 *  具体配置接收方法的处理，处理业务逻辑，可以实例化多个ServerHandler，
					 *  例如，负责编码的handler，负责解码的handler，负责业务的handler
					 *  
					 */
					//3.  给管道添加一系列Handler
					ch.pipeline().addLast(new ServerHandler());		
				}
				
			});
			
			/*
			 * Futrue模式，异步绑定。 
			 * 主线程继续往下走，多一个线程监听端口，主线程没有阻塞
			 */
			// 4. 绑定端口
			ChannelFuture cf1 = b.bind(port).sync();				// 绑定端口，开始接收进来的连接
			
			/*
			 * 阻塞当前线程，以保证服务器端的Channel的正常运行
			 * 相当于 Thread.sleep(Integer.MAX_VALUE);
			 * 但是当通道关闭后也会通知服务器端
			 */
			// 5. 设置cf1没结束，本线程不能关闭
			cf1.channel().closeFuture().sync();						// 等待服务器Socket关闭。
																	// 没有这句客户端连接不了，因为主线程走完了
			
//			ChannelFuture cf2 = b.bind(8764).sync();
//			cf2.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 6. 释放线程组的资源
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();	
		}
	}
}
