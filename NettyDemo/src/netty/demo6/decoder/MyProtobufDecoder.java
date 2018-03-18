package netty.demo6.decoder;

import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class MyProtobufDecoder extends ProtobufDecoder {

	public MyProtobufDecoder(MessageLite prototype) {
		super(prototype);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
//		System.out.println(2);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("cli:4");
		System.out.println("svr:2");
		super.channelRead(ctx, msg);
	}
	
	
	
}
