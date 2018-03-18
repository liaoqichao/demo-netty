package netty.demo6.encoder;

import java.util.List;

import com.google.protobuf.MessageLiteOrBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class MyProtobufEncoder extends ProtobufEncoder {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		System.out.println("cli:2");
		System.out.println("svr:7");
		super.write(ctx, msg, promise);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
		super.encode(ctx, msg, out);
	}

	
	
}
