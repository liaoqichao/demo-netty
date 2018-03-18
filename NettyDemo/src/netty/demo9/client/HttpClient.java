package netty.demo9.client;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpClient {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 12345;
			
	
	public static void main(String[] args) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new HttpRequestEncoder())
					.addLast(new HttpResponseDecoder())
//					.addLast("deflater", new HttpContentCompressor())
					.addLast(new HttpObjectAggregator(10 * 1024 * 1024))
					.addLast(new HttpClientHandler());
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
					}
				}
				
			}).sync();
			
			URI uri = new URI("http://127.0.0.1/demo9/module/action.do?user=zhangsan&psw=123");
			String msg = "age=18&gender=female";
			
			FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
					HttpMethod.POST, uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
//			FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
//					HttpMethod.GET, uri.toASCIIString());
			
			req.headers().set(HttpHeaderNames.HOST, HOST);
			req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
			req.headers().set(HttpHeaderNames.CONTENT_LENGTH, req.content().readableBytes());
			req.headers().set("messageType","normal");
			req.headers().set("bussinessType", "demo9");
			
			cf.channel().writeAndFlush(req);
			cf.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
		
	}

}
