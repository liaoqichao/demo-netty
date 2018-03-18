package netty.demo9.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {

	private static final int PORT = 12345;
	public static boolean isSSL = true;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_SNDBUF, 3 * 1024)
			.childOption(ChannelOption.SO_RCVBUF, 3 * 1024)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
//					if(HttpServer.isSSL){
//						SSLEngine engine = SecureChatSslContextFactory
//					}
					pipeline
					/*
					 * 当Server处理完消息后，需要向Client发送响应。那么需要把响应编码成字节，再发送出去。故添加HttpResponseEncoder处理器
					 */
					.addLast("encoder", new HttpResponseEncoder())
					.addLast("decoder", new HttpRequestDecoder())
//					.addLast("deflater", new HttpContentCompressor())
					/*
					 *  HttpObjectAggregator负责把多个HttpMessage组装成一个完整的Http请求或者响应
					 *  到底是组装成请求还是响应，则取决于它所处理的内容是请求的内容，还是响应的内容。
					 *  这其实可以通过Inbound和Outbound来判断，对于Server端而言，在Inbound 端接收请求，
					 *  在Outbound端返回响应
					 *  
					 *  注意，HttpObjectAggregator通道处理器必须放到HttpRequestDecoder或者HttpRequestEncoder后面。
					 */
					.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024))
					/*
					 * 压缩 
					 */
					.addLast("bussiness", new HttpServerHandler());
				}
				
			});
			
			b.bind(PORT).sync().channel().closeFuture().sync();
			
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}

}
