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
					 * ��Server��������Ϣ����Ҫ��Client������Ӧ����ô��Ҫ����Ӧ������ֽڣ��ٷ��ͳ�ȥ�������HttpResponseEncoder������
					 */
					.addLast("encoder", new HttpResponseEncoder())
					.addLast("decoder", new HttpRequestDecoder())
//					.addLast("deflater", new HttpContentCompressor())
					/*
					 *  HttpObjectAggregator����Ѷ��HttpMessage��װ��һ��������Http���������Ӧ
					 *  ��������װ����������Ӧ����ȡ���������������������������ݣ�������Ӧ�����ݡ�
					 *  ����ʵ����ͨ��Inbound��Outbound���жϣ�����Server�˶��ԣ���Inbound �˽�������
					 *  ��Outbound�˷�����Ӧ
					 *  
					 *  ע�⣬HttpObjectAggregatorͨ������������ŵ�HttpRequestDecoder����HttpRequestEncoder���档
					 */
					.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024))
					/*
					 * ѹ�� 
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
