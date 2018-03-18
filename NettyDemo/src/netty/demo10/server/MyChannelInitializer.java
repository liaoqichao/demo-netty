package netty.demo10.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// ... ×Ô¶¨Òåhandler
		ch.pipeline()
		.addLast(new StringDecoder())
		.addLast(new StringEncoder())
		.addLast(new BussinessServerHandler());
	}

}
