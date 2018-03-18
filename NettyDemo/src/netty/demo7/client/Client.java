package netty.demo7.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import netty.demo7.pojo.PkgEntity;

public class Client {

	protected final HashedWheelTimer timer = new HashedWheelTimer();
	private Bootstrap b;
	private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
	
	public void connect(String host, int port){
		// 1. 创建eventLoopGroup、创建bootstrap和初始化bootstrap
		EventLoopGroup group = new NioEventLoopGroup();
		b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
		
		// 2. 创建定时器timer，创建watchdog（handler）
		final ConnectionWatchdog watchdog = new ConnectionWatchdog(b, timer, port , host, true){

			// 3. 实现handlers接口，把本身和其他handler加到handlers数组，并返回handlers数组
			@Override
			public ChannelHandler[] handlers() {
				return new ChannelHandler[]{
					this,
					
					new ProtobufVarint32FrameDecoder(),
					new ProtobufDecoder(PkgEntity.Pkg.getDefaultInstance()),
					
					new ProtobufVarint32LengthFieldPrepender(),
					new ProtobufEncoder(),
					
					new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS),
					idleStateTrigger,

//					new StringDecoder(),
//					new StringEncoder(),
					new BussinessClientHandler()
				};
			}
			
		};
		
		ChannelFuture cf;
		// 4. 连接
		try {
			synchronized (b) {
				b.handler(new ChannelInitializer<SocketChannel>(){

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(watchdog.handlers());
					}
					
				});
				
				cf = b.connect(host, port);
			}
			cf.sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		int port = 12345;
		if(args != null && args.length > 0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
			}
		}
		new Client().connect("127.0.0.1", port);;
	}

}
