package netty.demo3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.demo3.decoder.JsonToMsgDecoder;
import netty.demo3.decoder.MessageDecoder;
import netty.demo3.encoder.MessageEncoder;
import netty.demo3.encoder.MsgToJsonEncoder;


/**
 * ByteToMessageDecoder			������
 * MessageToByteEncoder			������
 * MessageToMessageDecoder		������
 * MessageToMessageEncoder		������
 * MessageToMessageCodec		�������
 */

public class Server {
	
//	ByteToMessageDecoder : {"psw":"123","rcv":"","snd":"��ã�server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
//	MessageToMessageDecoder : Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=��ã�server, rcv=]
//	Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=��ã�server, rcv=�������յ�]
//	MessageEncoder : [B@55cb486b
//	MsgToJsonEncoder : {"psw":"123","rcv":"�������յ�","snd":"��ã�server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
	
	private static final int PORT = 12345;
	
	public static void main(String[] args){
		EventLoopGroup parent = new NioEventLoopGroup();
		EventLoopGroup child = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(parent,child)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_SNDBUF, 3 * 1024)
			.childOption(ChannelOption.SO_RCVBUF, 3 * 1024)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					// -- ���밴˳�� -- 
					.addLast(new MessageDecoder())
					.addLast(new JsonToMsgDecoder())
					
					// -- �������� --
					.addLast(new MsgToJsonEncoder())
					.addLast(new MessageEncoder())
					
					// -- ҵ����� -- 
					.addLast(new BussinessHandler());
					
				}
				
			});
			
			b.bind(PORT).sync().channel().closeFuture().sync();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			child.shutdownGracefully();
			parent.shutdownGracefully();
		}
	}
}
