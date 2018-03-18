package netty.demo5.server;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo5.pojo.MessageEntity;

public class Server {
	
	private static final int PORT = 12345;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					// 设置心跳机制，5秒查看一次在线客户端channel是否空闲
					// 5秒内channelRead方法没被调用就出发一次userEventTrigger()方法
					.addLast("timeout", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
					.addLast(new HeartBeatServerHandler())
					// -- begin protobuf 处理器 --
					// -- inbound 逆序 --
					// 配置protobuf解码处理器，收到消息会自动解码，protobufDecoder是netty自带的，参数是是实体类对象
					.addLast("protobufDecoder", new ProtobufDecoder(MessageEntity.Message.getDefaultInstance()))
					
					// 用于解码前解决半包和粘包的问题（利用包头中的包含数组长度识别半包和粘包）
					.addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
					
					// 配置protobuf编码器，发送消息会先经过编码
					.addLast("protobufEncoder", new ProtobufEncoder())
//					// -- end protobuf 处理器 --
					
					.addLast("rcvHandler", new RcvHandler()) // 自定义处理器，接收消息
					.addLast("ackHandler", new AckServerHandler());
					
					
				}
				
			});
			
			b.bind(PORT).sync().channel().closeFuture().sync();
			
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
		
	}
}
