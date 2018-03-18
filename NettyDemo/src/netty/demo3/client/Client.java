package netty.demo3.client;

import java.util.UUID;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.demo3.decoder.JsonToMsgDecoder;
import netty.demo3.decoder.MessageDecoder;
import netty.demo3.encoder.MessageEncoder;
import netty.demo3.encoder.MsgToJsonEncoder;
import netty.demo3.pojo.Message;


public class Client {
	
//	MessageEncoder : [B@2cd2ec7c
//	MsgToJsonEncoder : {"psw":"123","rcv":"","snd":"你好，server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
//	ByteToMessageDecoder : {"psw":"123","rcv":"服务器收到","snd":"你好，server","username":"zhangsan","validateCode":"1799ecae-d5d3-4409-8013-1a035c6b6a79"}
//	MessageToMessageDecoder : Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=你好，server, rcv=服务器收到]
//	clien.BussinessHandler : Message [username=zhangsan, psw=123, validateCode=1799ecae-d5d3-4409-8013-1a035c6b6a79, snd=你好，server, rcv=服务器收到]
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 12345;
	
	public static void main(String[] args) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_SNDBUF, 3 * 1024)
			.option(ChannelOption.SO_RCVBUF, 3 * 1024)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()

					// 解码
					.addLast(new MessageDecoder())
					.addLast(new JsonToMsgDecoder())
					
					// 编码
					.addLast(new MessageEncoder())
					.addLast(new MsgToJsonEncoder())
					
					// 业务
					.addLast(new BussinessHandler());

				}
				
			});
			
			ChannelFuture cf = b.connect(HOST, PORT).sync();
			Message msg = new Message();
			msg.setUsername("zhangsan");
			msg.setPsw("123");
			msg.setValidateCode(UUID.randomUUID().toString());
			msg.setSnd("你好，server");
			Channel channel = cf.channel();
			channel.writeAndFlush(msg);
			channel.closeFuture().sync();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
