package netty.demo6.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class MyProtobufVarint32LengthFieldPrepender extends ProtobufVarint32LengthFieldPrepender {

	
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		super.write(ctx, msg, promise);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		System.out.println("cli:1");
		System.out.println("svr:6");
		super.encode(ctx, msg, out);
	}

	
}
