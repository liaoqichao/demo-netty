package netty.demo5.client;

import java.util.UUID;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import netty.demo5.pojo.MessageEntity;

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
					// -- inbound 逆序
					// 解码处理器处理器
					.addLast("protobufDecoder", new ProtobufDecoder(MessageEntity.Message.getDefaultInstance()))
//					// 解码前解决半包和粘包问题的处理器
					.addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
					// 编码处理器
					.addLast("protobufEncoder", new ProtobufEncoder())
					// 业务
					.addLast("rcvHandler", new RcvHandler());
			
				}
				
			});
			
			ChannelFuture cf = b.connect(HOST, PORT).sync();
			
			MessageEntity.Message.Builder builder = MessageEntity.Message.newBuilder();
			builder.setUsername("zhangsan");
			builder.setPsw("123");
			builder.setValidateCode(UUID.randomUUID().toString());
			builder.setSnd("你好，服务器");
			MessageEntity.Message message = builder.build();
			
			cf.channel().writeAndFlush(message);
			cf.channel().closeFuture().sync();
			System.out.println("cli..");
		} finally {
			group.shutdownGracefully();
		}
	}

}
