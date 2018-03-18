package netty.demo7.server;

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
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo7.pojo.PkgEntity;

/**
 * 1. 开启服务器端
 * 2. 开启客户端，进行连接
 * 3. 关闭服务器端，客户端进行重连
 * 4. 开启服务器端，客户端重连成功
 *
 */
public class Server {

	private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
	private int port;
	
	public Server(int port){
		this.port = port;
	}
	
	public void start()throws Exception{
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_SNDBUF, 3 * 1024)
			.childOption(ChannelOption.SO_RCVBUF, 3 * 1024)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					
//					.addLast("decoder", new StringDecoder())
//					.addLast("encoder", new StringEncoder())
//					.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
//					.addLast(idleStateTrigger)
//					.addLast("handler", new BussinessHandler());
					
					.addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
					.addLast("protobufDecoder", new ProtobufDecoder(PkgEntity.Pkg.getDefaultInstance()))
					.addLast("prepender",new ProtobufVarint32LengthFieldPrepender())
					.addLast("protobufEncoder", new ProtobufEncoder()) 
					.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS))
					.addLast(idleStateTrigger)
					.addLast("handler", new BussinessHandler());
				}
				
			});
			
			b.bind(port).sync().channel().closeFuture().sync();
			
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new Server(12345).start();
	}

}
