package netty.demo6.server;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo6.decoder.MyProtobufDecoder;
import netty.demo6.decoder.MyProtobufVarint32FrameDecoder;
import netty.demo6.encoder.MyProtobufEncoder;
import netty.demo6.encoder.MyProtobufVarint32LengthFieldPrepender;
import netty.demo6.pojo.PkgEntity;

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
					
					// -- inbound顺序 -- 
					// 解码
					.addLast("frameDecoder", new MyProtobufVarint32FrameDecoder())
					.addLast("protobufDecoder", new MyProtobufDecoder(PkgEntity.Pkg.getDefaultInstance()))
				
					// 心跳
					.addLast(new IdleStateHandler(5,0,0,TimeUnit.MINUTES))
					.addLast("timeout", new HeartBeatServerHandler())	  
					
					// -- outbound 逆序 --
					// 编码
					.addLast("prepender",new MyProtobufVarint32LengthFieldPrepender())
					.addLast("protobufEncoder", new MyProtobufEncoder()) 
					
					// -- 业务（inbound顺序） --
					// 业务
					.addLast("rcvHandler", new RcvHandler())
					.addLast("ackHandler", new AckHandler());
				}

				
			});
			
			b.bind(PORT).sync().channel().closeFuture().sync();
			
			
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}

}
