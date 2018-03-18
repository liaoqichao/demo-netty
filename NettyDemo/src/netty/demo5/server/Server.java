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
					// �����������ƣ�5��鿴һ�����߿ͻ���channel�Ƿ����
					// 5����channelRead����û�����þͳ���һ��userEventTrigger()����
					.addLast("timeout", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
					.addLast(new HeartBeatServerHandler())
					// -- begin protobuf ������ --
					// -- inbound ���� --
					// ����protobuf���봦�������յ���Ϣ���Զ����룬protobufDecoder��netty�Դ��ģ���������ʵ�������
					.addLast("protobufDecoder", new ProtobufDecoder(MessageEntity.Message.getDefaultInstance()))
					
					// ���ڽ���ǰ��������ճ�������⣨���ð�ͷ�еİ������鳤��ʶ������ճ����
					.addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
					
					// ����protobuf��������������Ϣ���Ⱦ�������
					.addLast("protobufEncoder", new ProtobufEncoder())
//					// -- end protobuf ������ --
					
					.addLast("rcvHandler", new RcvHandler()) // �Զ��崦������������Ϣ
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
