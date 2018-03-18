package netty.demo6.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class MyProtobufVarint32FrameDecoder extends ProtobufVarint32FrameDecoder {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
//		System.out.println(1);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("cli:3");
		System.out.println("svr:1");
		super.channelRead(ctx, msg);
	}
	
}
