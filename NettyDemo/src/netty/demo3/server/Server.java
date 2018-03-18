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
 * ByteToMessageDecoder			解码器
 * MessageToByteEncoder			编码器
 * MessageToMessageDecoder		解码器
 * MessageToMessageEncoder		编码器
 * MessageToMessageCodec		编解码器
 */

public class Server {
	
//	ByteToMessageDecoder : {"psw":"123","rcv":"","snd":"你好，server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
//	MessageToMessageDecoder : Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=你好，server, rcv=]
//	Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=你好，server, rcv=服务器收到]
//	MessageEncoder : [B@55cb486b
//	MsgToJsonEncoder : {"psw":"123","rcv":"服务器收到","snd":"你好，server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
	
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
					// -- 解码按顺序 -- 
					.addLast(new MessageDecoder())
					.addLast(new JsonToMsgDecoder())
					
					// -- 编码逆序 --
					.addLast(new MsgToJsonEncoder())
					.addLast(new MessageEncoder())
					
					// -- 业务最后 -- 
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
