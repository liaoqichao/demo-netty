package netty.demo6.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo6.decoder.MyProtobufDecoder;
import netty.demo6.decoder.MyProtobufVarint32FrameDecoder;
import netty.demo6.encoder.MyProtobufEncoder;
import netty.demo6.encoder.MyProtobufVarint32LengthFieldPrepender;
import netty.demo6.pojo.PkgEntity;

public class Client {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 12345;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					
					// 解码
					.addLast("frameDecoder", new MyProtobufVarint32FrameDecoder())
					.addLast("protobufDecoder", new MyProtobufDecoder(PkgEntity.Pkg.getDefaultInstance()))
					
					// 心跳
					.addLast("timeout", new IdleStateHandler(0,5,0,TimeUnit.SECONDS))
					.addLast(new HeartBeatClientHandler())
					
					// 编码
					.addLast("prepender",new MyProtobufVarint32LengthFieldPrepender())
					.addLast("protobufEncoder", new MyProtobufEncoder())
					
					// 业务
					.addLast("rcvHandler", new RcvHandler());
					
				}
				
			});
			
			ChannelFuture cf = b.connect(HOST, PORT).sync();
				
			PkgEntity.Pkg.Builder builder = PkgEntity.Pkg.newBuilder();
			builder.setData("你好，服务器");
			builder.setReqType(0);
			PkgEntity.Pkg pkg = builder.build();
			cf.channel().writeAndFlush(pkg);
			cf.channel().closeFuture().sync();
			
		} finally {
			group.shutdownGracefully();
		}
	}
}
