package NettyDemo.NettyDemo11.chatroom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebChatServer {
	
	private int port;
	
	public WebChatServer(int port) {
		this.port = port;
	}
	
	public void start() throws InterruptedException {
		// 1. 定义2个线程组
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)
			.handler(new LoggingHandler())
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast("http-codec", new HttpServerCodec())	// 将请求和响应编码或解码成http协议消息
					.addLast("aggregator",new HttpObjectAggregator(65535))	// 把多个HTTP请求中的数据组装成一个，接收数据最大长度
					.addLast("chunkedWrite",new ChunkedWriteHandler())	// 这个handler主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的
					.addLast("httpRequest",new HttpRequestHandler("/chat"))	// 自定义handler,添加参数，如果是/chat则是webSocket请求，如果不是则是http请求
					.addLast("websocket", new WebSocketServerProtocolHandler("/chat")) // 路径带/chat都认为是websocket协议
					.addLast("textWebSocketFrameHandler", new TextWebSocketFrameHandler()); // 聊天业务了逻辑,处理纯文本形式websocket数据帧,netty有自带的TextWebSocketFrame
				}
				
			});
			
			System.out.println("服务器端已启动...");
			b.bind(port).sync().channel().closeFuture().sync();
			
			
		} finally {
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new WebChatServer(12345).start();
//		System.out.println(new WebChatServer(12345).getClass().getProtectionDomain().getCodeSource().getLocation());
	}
}
