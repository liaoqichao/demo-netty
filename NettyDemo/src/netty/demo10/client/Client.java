package netty.demo10.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class Client {

	private static final  String HOST = "127.0.0.1";
	private static final  int PORT = 12345;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new StringDecoder(CharsetUtil.UTF_8))
					.addLast(new StringEncoder(CharsetUtil.UTF_8))
					.addLast(new BussinessClientHandler());
				}
				
			});
			
			ChannelFuture cf = b.connect(HOST, PORT);
			cf.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						System.out.println("连接成功");
					} else{
						System.out.println("连接失败");
						group.shutdownGracefully();
					}
				}
				
			});
			String req = "hello server";
			ChannelFuture cf1 = cf.channel().writeAndFlush(Unpooled.copiedBuffer(req.getBytes(CharsetUtil.UTF_8)));
			cf1.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						System.out.println("发送成功");
					} else{
						System.out.println("发送失败");
					}
				}
				
			});
			cf1.channel().closeFuture().sync();
		} finally {
			
		}
	}

}
